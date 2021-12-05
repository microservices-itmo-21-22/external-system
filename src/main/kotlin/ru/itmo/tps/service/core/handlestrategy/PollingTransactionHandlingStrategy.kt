package ru.itmo.tps.service.core.handlestrategy

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.management.Account
import ru.itmo.tps.entity.management.AnswerMethod
import ru.itmo.tps.exception.TransactionSubmittingFailureException
import ru.itmo.tps.service.core.limithandler.LimitHandlerChainBuilder
import ru.itmo.tps.service.core.limithandler.impl.ServerErrorsLimitHandler
import ru.itmo.tps.service.core.ratelimits.RateLimitsService
import ru.itmo.tps.service.management.TransactionService

@Service
class PollingTransactionHandlingStrategy(
    private val nonblockingTransactionDispatcher: CoroutineDispatcher,
    private val transactionService: TransactionService,
    private val rateLimitsService: RateLimitsService
) : TransactionHandlingStrategy {

    override fun supports(account: Account) = AnswerMethod.POLLING == account.answerMethod

    override suspend fun handle(transaction: Transaction, account: Account): Transaction {
        rateLimitsService.acquire(account)
        val limitHandlerChainBuilder = LimitHandlerChainBuilder(account.accountLimits)

        limitHandlerChainBuilder.enableResponseTimeVariation()
        limitHandlerChainBuilder.enableTransactionFailure()

        try {
            ServerErrorsLimitHandler.create(account.accountLimits).handle(transaction)
        } catch (e: TransactionSubmittingFailureException) {
            rateLimitsService.release(account)
            throw e
        }
        transactionService.cachePending(transaction)

        CoroutineScope(nonblockingTransactionDispatcher).launch {
            val handledTransaction = limitHandlerChainBuilder.build()
                .handle(transaction)
                .complete(account.transactionCost)

            transactionService.saveAndEvict(handledTransaction)
            rateLimitsService.release(account)
        }

        return transaction
    }
}
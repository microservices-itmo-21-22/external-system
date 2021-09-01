package ru.itmo.tps.service.core.handlestrategy

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.management.Account
import ru.itmo.tps.entity.management.AnswerMethod
import ru.itmo.tps.service.core.limithandler.LimitHandlerChainBuilder
import ru.itmo.tps.service.core.limithandler.impl.ServerErrorsLimitHandler
import ru.itmo.tps.service.management.TransactionService

@Service
class CallbackTransactionHandlingStrategy(
    private val nonblockingTransactionDispatcher: CoroutineDispatcher,
    private val transactionService: TransactionService
) : TransactionHandlingStrategy {
    private val logger = KotlinLogging.logger {}

    override fun supports(account: Account) = AnswerMethod.CALLBACK == account.answerMethod

    override suspend fun handle(transaction: Transaction, account: Account): Transaction {
        val limitHandlerChainBuilder = LimitHandlerChainBuilder(account.accountLimits)

        limitHandlerChainBuilder.enableResponseTimeVariation()
        limitHandlerChainBuilder.enableTransactionFailure()
        limitHandlerChainBuilder.enableRateLimiter()

        ServerErrorsLimitHandler.create(account.accountLimits).handle(transaction)

        CoroutineScope(nonblockingTransactionDispatcher).launch {
            val handledTransaction = limitHandlerChainBuilder.build().handle(transaction).complete()
            transactionService.save(handledTransaction) // todo sukhoa still better use different pool
            kotlin.runCatching {
                RestTemplate().postForLocation( // todo sukhoa it should be done in non-blocking way. get the future and suspend coroutine
                    account.callbackUrl!!, // TODO: 29.08.2021 :)
                    handledTransaction // todo sukhoa configure timeout
                )
            }.onFailure { logger.warn { "Cannot reach callback server" } }
        }

        return transaction // todo sukhoa thi si not like fair transaction but rater submission
    }
}
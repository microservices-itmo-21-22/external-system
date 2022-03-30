package ru.itmo.tps.service.core.handlestrategy

import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.management.Account
import ru.itmo.tps.entity.management.AnswerMethod
import ru.itmo.tps.service.core.limithandler.LimitHandlerChainBuilder
import ru.itmo.tps.service.core.ratelimits.RateLimitsService
import ru.itmo.tps.service.management.TransactionService

@Service
@RequiredArgsConstructor
class BlockingTransactionHandlingStrategy(private val transactionService: TransactionService,
                                          private val rateLimitsService: RateLimitsService) :
    TransactionHandlingStrategy {

    override fun supports(account: Account) = account.answerMethod == AnswerMethod.TRANSACTION

    override suspend fun handle(transaction: Transaction, account: Account): Transaction {
        try {
            rateLimitsService.acquire(account)

            val limitHandlerChainBuilder = LimitHandlerChainBuilder(account.accountLimits)

            limitHandlerChainBuilder.enableServerErrors()
            limitHandlerChainBuilder.enableResponseTimeVariation()
            limitHandlerChainBuilder.enableTransactionFailure()

            val handlerChain = limitHandlerChainBuilder.build()
            val completedTransaction = handlerChain.handle(transaction).complete(account.transactionCost)

            transactionService.save(completedTransaction)

            return completedTransaction
        } finally {
            rateLimitsService.release(account)
        }
    }
}
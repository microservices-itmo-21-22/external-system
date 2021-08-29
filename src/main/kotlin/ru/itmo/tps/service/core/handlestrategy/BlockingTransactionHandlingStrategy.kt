package ru.itmo.tps.service.core.handlestrategy

import org.springframework.stereotype.Service
import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.management.Account
import ru.itmo.tps.entity.management.AnswerMethod
import ru.itmo.tps.service.core.limithandler.LimitHandlerChainBuilder

@Service
class BlockingTransactionHandlingStrategy : TransactionHandlingStrategy {
    override fun supports(account: Account) = account.answerMethod == AnswerMethod.TRANSACTION

    override suspend fun handle(transaction: Transaction, account: Account): Transaction {
        val limitHandlerChainBuilder = LimitHandlerChainBuilder(account.accountLimits)

        limitHandlerChainBuilder.enableServerErrors()
        limitHandlerChainBuilder.enableResponseTimeVariation()
        limitHandlerChainBuilder.enableTransactionFailure()
        limitHandlerChainBuilder.enableRateLimiter()

        return limitHandlerChainBuilder.build().handle(transaction).complete()
    }
}
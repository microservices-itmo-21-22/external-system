package ru.itmo.tps.service.core.handlestrategy

import org.springframework.stereotype.Service
import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.management.Account
import ru.itmo.tps.dto.management.AccountLimits
import ru.itmo.tps.entity.management.AnswerMethod
import ru.itmo.tps.service.core.limithandler.LimitHandlerBuilder

@Service
class BlockingTransactionHandlingStrategy : TransactionHandlingStrategy {
    override fun supports(account: Account) = account.answerMethod == AnswerMethod.TRANSACTION


    override fun handle(transaction: Transaction, accountLimits: AccountLimits) {
        val limitHandlerBuilder = LimitHandlerBuilder(accountLimits)

        limitHandlerBuilder.enableServerErrors()
        limitHandlerBuilder.enableResponseTimeVariation()
        limitHandlerBuilder.enableTransactionFailure()

        limitHandlerBuilder.build().handle(transaction)
    }
}
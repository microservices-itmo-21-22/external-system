package ru.itmo.tps.service.core.handlestrategy

import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.management.Account
import ru.itmo.tps.dto.management.AccountLimits
import ru.itmo.tps.entity.management.AnswerMethod

class PollingTransactionHandlingStrategy : TransactionHandlingStrategy {
    override fun supports(account: Account) = AnswerMethod.POLLING == account.answerMethod

    override fun handle(transaction: Transaction, accountLimits: AccountLimits) {
        TODO("Not yet implemented")
    }
}
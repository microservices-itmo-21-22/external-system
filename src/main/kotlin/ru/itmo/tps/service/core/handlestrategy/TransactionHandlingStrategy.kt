package ru.itmo.tps.service.core.handlestrategy

import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.management.Account
import ru.itmo.tps.dto.management.AccountLimits

interface TransactionHandlingStrategy {
    fun supports(account: Account): Boolean

    fun handle(transaction: Transaction, accountLimits: AccountLimits)
}
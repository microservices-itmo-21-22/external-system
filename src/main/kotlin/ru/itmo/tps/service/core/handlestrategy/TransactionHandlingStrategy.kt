package ru.itmo.tps.service.core.handlestrategy

import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.management.Account

interface TransactionHandlingStrategy {
    fun supports(account: Account): Boolean

    suspend fun handle(transaction: Transaction, account: Account): Transaction
}
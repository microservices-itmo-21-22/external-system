package ru.itmo.tps.service.core.limithandler.impl

import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.service.core.limithandler.LimitHandler

class NoOperationLimitHandler : LimitHandler {
    override suspend fun handle(transaction: Transaction): Transaction = transaction
}
package ru.itmo.tps.service.core.limithandler.impl

import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.TransactionStatus
import ru.itmo.tps.service.core.limithandler.LimitHandler
import java.time.Instant

class NoLimitationsLimitHandler : LimitHandler {
    override fun handle(transaction: Transaction): Transaction {
        if (transaction.status == TransactionStatus.PENDING) {
            transaction.status = TransactionStatus.SUCCESS
        }

        val now = Instant.now().toEpochMilli()
        transaction.completedTime = now
        transaction.delta = now - transaction.submitTime

        return transaction
    }
}
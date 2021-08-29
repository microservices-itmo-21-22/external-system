package ru.itmo.tps.service.core.limithandler.impl

import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.TransactionStatus
import ru.itmo.tps.dto.management.AccountLimits
import ru.itmo.tps.service.core.limithandler.LimitHandler
import kotlin.random.Random

class TransactionFailureLimitHandler private constructor(
    private val limitHandler: LimitHandler,
    private val failureProbability: Double,
) : LimitHandler {
    companion object {
        fun create(limitHandler: LimitHandler, accountLimits: AccountLimits): LimitHandler {
            if (!accountLimits.enableFailures) return limitHandler

            return TransactionFailureLimitHandler(
                limitHandler = limitHandler,
                failureProbability = accountLimits.failureProbability
            )
        }
    }

    override fun handle(transaction: Transaction): Transaction {
        val random = Random.nextDouble(0.0, 100.0)

        if (random < failureProbability) {
            transaction.status = TransactionStatus.FAILURE
        }

        return limitHandler.handle(transaction)
    }
}
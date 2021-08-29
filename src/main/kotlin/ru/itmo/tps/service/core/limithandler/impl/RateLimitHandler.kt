package ru.itmo.tps.service.core.limithandler.impl

import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.management.AccountLimits
import ru.itmo.tps.service.core.limithandler.LimitHandler

class RateLimitHandler private constructor(
    private val requestsPerSecond: Long,
) : LimitHandler {
    companion object {
        fun create(accountLimits: AccountLimits): LimitHandler {
            if (!accountLimits.enableRateLimits) return NoOperationLimitHandler()

            return RateLimitHandler(accountLimits.requestsPerSecond)
        }
    }

    override suspend fun handle(transaction: Transaction): Transaction {
        println("RateLimitHandler invoked. RPS: $requestsPerSecond")

        return transaction
    }
}
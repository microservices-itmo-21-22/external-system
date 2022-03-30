package ru.itmo.tps.service.core.limithandler.impl

import kotlinx.coroutines.delay
import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.management.AccountLimits
import ru.itmo.tps.service.core.limithandler.LimitHandler
import kotlin.random.Random.Default.nextLong

class ResponseTimeVariationLimitHandler private constructor(
    private val minResponseTime: Long,
    private val maxResponseTime: Long,
) : LimitHandler {
    companion object {
        fun create(accountLimits: AccountLimits): LimitHandler {
            if (!accountLimits.enableResponseTimeVariation) return NoOperationLimitHandler()

            return ResponseTimeVariationLimitHandler(
                minResponseTime = accountLimits.responseTimeLowerBound,
                maxResponseTime = accountLimits.responseTimeUpperBound
            )
        }
    }

    override suspend fun handle(transaction: Transaction): Transaction {
        val sleepMillis = nextLong(minResponseTime, maxResponseTime)

        delay(sleepMillis) // todo sukhoa shouldn't we add this details to transaction? in order to make it easier to investigate different situations

        return transaction
    }
}
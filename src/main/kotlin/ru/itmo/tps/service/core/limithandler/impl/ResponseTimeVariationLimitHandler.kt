package ru.itmo.tps.service.core.limithandler.impl

import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.management.AccountLimits
import ru.itmo.tps.service.core.limithandler.LimitHandler
import kotlin.random.Random.Default.nextLong

class ResponseTimeVariationLimitHandler private constructor(
    private val limitHandler: LimitHandler,
    private val minResponseTime: Long,
    private val maxResponseTime: Long,
) : LimitHandler {
    companion object {
        fun create(limitHandler: LimitHandler, accountLimits: AccountLimits): LimitHandler {
            if (!accountLimits.enableResponseTimeVariation) return limitHandler

            return ResponseTimeVariationLimitHandler(
                limitHandler = limitHandler,
                minResponseTime = accountLimits.responseTimeLowerBound,
                maxResponseTime = accountLimits.responseTimeUpperBound
            )
        }
    }

    override fun handle(transaction: Transaction): Transaction {
        val sleepMillis = nextLong(minResponseTime, maxResponseTime)

        Thread.sleep(sleepMillis)

        return limitHandler.handle(transaction)
    }
}
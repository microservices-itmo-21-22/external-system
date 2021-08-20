package ru.itmo.tps.service.core.limithandler.impl

import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.management.AccountLimits
import ru.itmo.tps.exception.TransactionSubmittingFailureException
import ru.itmo.tps.service.core.limithandler.LimitHandler
import kotlin.random.Random

class ServerErrorsLimitHandler private constructor(
    private val limitHandler: LimitHandler,
    private val serverErrorProbability: Double,
) : LimitHandler {
    companion object {
        fun create(limitHandler: LimitHandler, accountLimits: AccountLimits): LimitHandler {
            if (!accountLimits.enableServerErrors) return limitHandler

            return ServerErrorsLimitHandler(
                limitHandler = limitHandler,
                serverErrorProbability = accountLimits.serverErrorProbability
            )
        }
    }

    override fun handle(transaction: Transaction): Transaction {
        val random = Random.nextDouble(0.0, 100.0)

        if (random < serverErrorProbability) {
            throw TransactionSubmittingFailureException("Unable to submit transaction with id=${transaction.id}")
        }

        return limitHandler.handle(transaction)
    }
}
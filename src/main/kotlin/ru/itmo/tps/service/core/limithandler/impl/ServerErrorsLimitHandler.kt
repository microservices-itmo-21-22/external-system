package ru.itmo.tps.service.core.limithandler.impl

import mu.KotlinLogging
import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.management.AccountLimits
import ru.itmo.tps.exception.TransactionSubmittingFailureException
import ru.itmo.tps.service.core.limithandler.LimitHandler
import kotlin.random.Random

class ServerErrorsLimitHandler private constructor(
    private val serverErrorProbability: Double,
) : LimitHandler {
    private val logger = KotlinLogging.logger{}

    companion object {
        fun create(accountLimits: AccountLimits): LimitHandler {
            if (!accountLimits.enableServerErrors) return NoOperationLimitHandler()

            return ServerErrorsLimitHandler(serverErrorProbability = accountLimits.serverErrorProbability)
        }
    }

    override suspend fun handle(transaction: Transaction): Transaction {
        val random = Random.nextDouble(0.0, 100.0)

        if (random < serverErrorProbability) {
            throw TransactionSubmittingFailureException("Unable to submit transaction with id=${transaction.id}")
        }

        return transaction
    }
}
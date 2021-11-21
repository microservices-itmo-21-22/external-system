package ru.itmo.tps.service.core.limithandler.impl

import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import mu.KotlinLogging
import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.management.AccountLimits
import ru.itmo.tps.event.NotableEvents
import ru.itmo.tps.exception.TransactionSubmittingFailureException
import ru.itmo.tps.service.core.limithandler.LimitHandler
import kotlin.random.Random

class ServerErrorsLimitHandler private constructor(
    private val serverErrorProbability: Double,
) : LimitHandler {
    private val logger = KotlinLogging.logger {}
    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    companion object {
        fun create(accountLimits: AccountLimits): LimitHandler {
            if (!accountLimits.enableServerErrors) return NoOperationLimitHandler()

            return ServerErrorsLimitHandler(serverErrorProbability = accountLimits.serverErrorProbability)
        }
    }

    override suspend fun handle(transaction: Transaction): Transaction {
        val random = Random.nextDouble(0.0, 100.0)

        if (random < serverErrorProbability) {
            //eventLogger.error(NotableEvents.E_TRANSACTION_SUBMISSION_ERROR, transaction.id)
            throw TransactionSubmittingFailureException(
                NotableEvents.E_TRANSACTION_SUBMISSION_ERROR.getTemplate().format(transaction.id)
            )
        }

        return transaction
    }
}
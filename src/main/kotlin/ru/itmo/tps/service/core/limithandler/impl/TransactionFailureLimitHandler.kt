package ru.itmo.tps.service.core.limithandler.impl

import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.TransactionStatus
import ru.itmo.tps.dto.management.AccountLimits
import ru.itmo.tps.event.NotableEvents
import ru.itmo.tps.service.core.limithandler.LimitHandler
import kotlin.random.Random

class TransactionFailureLimitHandler private constructor(
    private val failureProbability: Double,
) : LimitHandler {

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    companion object {
        fun create(accountLimits: AccountLimits): LimitHandler {
            if (!accountLimits.enableFailures) return NoOperationLimitHandler()

            return TransactionFailureLimitHandler(accountLimits.failureProbability)
        }
    }

    override suspend fun handle(transaction: Transaction): Transaction {
        val random = Random.nextDouble(0.0, 100.0)

        if (random < failureProbability) {
            eventLogger.info(NotableEvents.I_TRANSACTION_FAILURE, transaction.id)
            return Transaction(
                id = transaction.id,
                status = TransactionStatus.FAILURE,
                submitTime = transaction.submitTime,
                accountId = transaction.accountId
            )
        }

        return transaction
    }
}
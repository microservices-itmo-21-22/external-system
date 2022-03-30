package ru.itmo.tps.service.core

import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import lombok.RequiredArgsConstructor
import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.TransactionRequest
import ru.itmo.tps.dto.TransactionStatus
import ru.itmo.tps.dto.management.Account
import ru.itmo.tps.event.NotableEvents
import ru.itmo.tps.exception.EntityNotFoundException
import ru.itmo.tps.exception.NotAuthenticatedException
import ru.itmo.tps.service.core.handlestrategy.TransactionHandlingStrategy
import ru.itmo.tps.service.management.AccountService
import java.util.*

@Service
@RequiredArgsConstructor
class TransactionHandler(
    private val accountService: AccountService,
    private val transactionHandlingStrategies: List<TransactionHandlingStrategy>
) {

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger
    private val log = KotlinLogging.logger {}


    suspend fun submitTransaction(transactionRequest: TransactionRequest): Transaction {
        eventLogger.info(NotableEvents.I_TRANSACTION_PROCESSING_STARTED, transactionRequest.clientSecret)
        val account: Account
        try {
            account = accountService.findByClientSecret(transactionRequest.clientSecret)
        } catch (e: EntityNotFoundException) {
            log.warn { "Account for client secret '${transactionRequest.clientSecret}' not found" }
            throw NotAuthenticatedException("Cannot find account with given client secret")
        }

        var transaction = Transaction(
            id = UUID.randomUUID(),
            status = TransactionStatus.PENDING,
            accountId = account.id,
            completedTime = null
        )

        transaction = selectStrategy(account).handle(transaction, account)

        if (transaction.status == TransactionStatus.SUCCESS)
            eventLogger.info(NotableEvents.I_TRANSACTION_SUCCEEDED, transaction.id)
        return transaction
    }

    private fun selectStrategy(account: Account): TransactionHandlingStrategy {
        return transactionHandlingStrategies.find { it.supports(account) }
            ?: throw Exception("Cannot find suitable handler")
    }
}
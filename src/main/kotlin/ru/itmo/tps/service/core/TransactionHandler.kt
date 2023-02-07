package ru.itmo.tps.service.core

import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import lombok.RequiredArgsConstructor
import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.TransactionRequest
import ru.itmo.tps.dto.TransactionStatus
import ru.itmo.tps.dto.TransactionType
import ru.itmo.tps.dto.management.Account
import ru.itmo.tps.event.NotableEvents
import ru.itmo.tps.exception.EntityNotFoundException
import ru.itmo.tps.exception.NotAuthenticatedException
import ru.itmo.tps.service.core.handlestrategy.TransactionHandlingStrategy
import ru.itmo.tps.service.core.metrics.TransactionMetrics
import ru.itmo.tps.service.management.AccountService
import java.util.*

@Service
@RequiredArgsConstructor
class TransactionHandler(
    private val accountService: AccountService,
    private val transactionHandlingStrategies: List<TransactionHandlingStrategy>,
    private val transactionMetrics: TransactionMetrics
) {

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger
    private val log = KotlinLogging.logger {}


    suspend fun submitTransaction(transactionRequest: TransactionRequest,
                                  transactionType: TransactionType): Transaction {
        eventLogger.info(NotableEvents.I_TRANSACTION_PROCESSING_STARTED, transactionRequest.clientSecret)
        val account: Account = try {
            accountService.findByClientSecret(transactionRequest.clientSecret)
        } catch (e: EntityNotFoundException) {
            log.warn { "Account for client secret '${transactionRequest.clientSecret}' not found" }
            throw NotAuthenticatedException("Cannot find account with given client secret")
        }

        val transaction = Transaction(
            id = UUID.randomUUID(),
            status = TransactionStatus.PENDING,
            accountId = account.id,
            completedTime = null
        )

        return runCatching {
            transactionMetrics.executeTransactionTimed(transactionType, account.name) {
                selectStrategy(account).handle(transaction, account)
            }.also {
                when (it.status) {
                    TransactionStatus.SUCCESS -> {
                        eventLogger.info(NotableEvents.I_TRANSACTION_SUCCEEDED, transaction.id)
                        transactionMetrics.countSuccessfulTransaction(transactionType, account.name)
                        transactionMetrics.countSpentMoney(
                            transactionType,
                            account.name,
                            it.cost?.toDouble() ?: 0.0
                        )
                    }
                    else -> transactionMetrics.countFailedTransaction(transactionType, account.name)
                }
            }
        }.onFailure { transactionMetrics.countTransactionError(transactionType, account.name) }.getOrThrow()
    }

    private fun selectStrategy(account: Account): TransactionHandlingStrategy =
        transactionHandlingStrategies.find { it.supports(account) }
            ?: throw Exception("Cannot find suitable handler")
}
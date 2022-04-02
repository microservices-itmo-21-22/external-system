package ru.itmo.tps.service.core

import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.TransactionRequest
import ru.itmo.tps.dto.TransactionStatus
import ru.itmo.tps.dto.management.Account
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
    suspend fun submitTransaction(transactionRequest: TransactionRequest): Transaction {
        val account: Account = try {
            accountService.findByClientSecret(transactionRequest.clientSecret)
        } catch (e: EntityNotFoundException) {
            throw NotAuthenticatedException("Cannot find account with given client secret")
        }

        val transaction = Transaction(
            id = UUID.randomUUID(),
            status = TransactionStatus.PENDING,
            accountId = account.id,
            completedTime = null
        )

        return runCatching {
            transactionMetrics.executeTransactionTimed(account.name) {
                selectStrategy(account).handle(transaction, account)
            }.also {
                when (it.status) {
                    TransactionStatus.SUCCESS -> {
                        transactionMetrics.countSuccessfulTransaction(account.name)
                        transactionMetrics.countSpentMoney(account.name, it.cost?.toDouble() ?: 0.0)
                    }
                    else -> transactionMetrics.countFailedTransaction(account.name)
                }
            }
        }.onFailure { transactionMetrics.countTransactionError(account.name) }.getOrThrow()
    }

    private fun selectStrategy(account: Account): TransactionHandlingStrategy =
        transactionHandlingStrategies.find { it.supports(account) }
            ?: throw Exception("Cannot find suitable handler")
}
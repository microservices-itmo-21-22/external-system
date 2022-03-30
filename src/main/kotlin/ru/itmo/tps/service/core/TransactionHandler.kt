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
import ru.itmo.tps.service.management.AccountService
import java.util.*

@Service
@RequiredArgsConstructor
class TransactionHandler(
    private val accountService: AccountService,
    private val transactionHandlingStrategies: List<TransactionHandlingStrategy>
) {
    suspend fun submitTransaction(transactionRequest: TransactionRequest): Transaction {
        val account: Account
        try {
            account = accountService.findByClientSecret(transactionRequest.clientSecret)
        } catch (e: EntityNotFoundException) {
            throw NotAuthenticatedException("Cannot find account with given client secret")
        }

        var transaction = Transaction(
            id = UUID.randomUUID(),
            status = TransactionStatus.PENDING,
            accountId = account.id,
            completedTime = null
        )

        transaction = selectStrategy(account).handle(transaction, account)

        return transaction
    }

    private fun selectStrategy(account: Account): TransactionHandlingStrategy {
        return transactionHandlingStrategies.find { it.supports(account) }
            ?: throw Exception("Cannot find suitable handler")
    }
}
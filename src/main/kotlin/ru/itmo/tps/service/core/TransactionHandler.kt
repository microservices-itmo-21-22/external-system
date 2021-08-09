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

@Service
@RequiredArgsConstructor
class TransactionHandler(
    private val accountService: AccountService,
    private val transactionHandlingStrategies: List<TransactionHandlingStrategy>
) {
    fun submitTransaction(transactionRequest: TransactionRequest): Transaction {
        val account: Account
        try {
            account = accountService.findByClientSecret(transactionRequest.clientSecret)
        } catch (e: EntityNotFoundException) {
            throw NotAuthenticatedException("Cannot find account with given client secret")
        }

        val transaction = Transaction(transactionRequest.transactionId, TransactionStatus.PENDING)

        selectStrategy(account).handle(transaction, account.accountLimits)

        return transaction
    }

    private fun selectStrategy(account: Account): TransactionHandlingStrategy {
        return transactionHandlingStrategies.find { it.supports(account) }
            ?: throw Exception("Cannot find suitable handler")
    }
}
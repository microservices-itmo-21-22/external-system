package ru.itmo.tps.service.core

import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.TransactionRequest
import ru.itmo.tps.dto.TransactionStatus
import ru.itmo.tps.service.core.handlestrategy.TransactionHandlingStrategy
import ru.itmo.tps.service.management.AccountService

@Service
@RequiredArgsConstructor
class TransactionHandler(
    private val accountService: AccountService,
    private val transactionHandlingStrategies: List<TransactionHandlingStrategy>
) {
    fun submitTransaction(transactionRequest: TransactionRequest): Transaction {
        val account = accountService.findByClientSecret(transactionRequest.clientSecret)

        var transaction = Transaction(transactionRequest.transactionId, TransactionStatus.PENDING)

        return transaction
    }
}
package ru.itmo.tps.service.core

import org.springframework.stereotype.Service
import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.TransactionRequest
import ru.itmo.tps.dto.TransactionStatus

@Service
class TransactionHandler {
    fun submitTransaction(transactionRequest: TransactionRequest): Transaction {
        var transaction = Transaction(transactionRequest.transactionId, TransactionStatus.PENDING)

        return transaction
    }
}
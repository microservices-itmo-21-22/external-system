package ru.itmo.tps.controller.core

import org.springframework.web.bind.annotation.*
import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.TransactionRequest
import ru.itmo.tps.service.core.TransactionHandler

@RestController
@RequestMapping("transactions")
class TransactionController(private val transactionHandler: TransactionHandler) {
    @PostMapping
    fun submitTransaction(@RequestBody transactionRequest: TransactionRequest) : Transaction {
        return transactionHandler.submitTransaction(transactionRequest)
    }
}
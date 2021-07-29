package ru.itmo.tps.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
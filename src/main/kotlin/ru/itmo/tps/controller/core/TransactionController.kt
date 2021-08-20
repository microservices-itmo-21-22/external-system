package ru.itmo.tps.controller.core

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.itmo.tps.dto.TransactionRequest
import ru.itmo.tps.service.core.TransactionHandler

@RestController
@RequestMapping("transactions")
class TransactionController(private val transactionHandler: TransactionHandler) {

    @PostMapping
    @DelicateCoroutinesApi
    fun submitTransactionAsync(@RequestBody transactionRequest: TransactionRequest) = GlobalScope.async {
        transactionHandler.submitTransaction(transactionRequest)
    }
}
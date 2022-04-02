package ru.itmo.tps.controller.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import org.springframework.web.bind.annotation.*
import ru.itmo.tps.config.coroutineExceptionHandler
import ru.itmo.tps.dto.PaymentTransactionRequest
import ru.itmo.tps.dto.TransactionRequest
import ru.itmo.tps.service.core.TransactionHandler
import ru.itmo.tps.service.management.TransactionService
import java.util.*

@RestController
@RequestMapping("/transactions/payment")
class PaymentTransactionController (
    private val transactionHandler: TransactionHandler,
    private val transactionDispatcher: CoroutineDispatcher,
    private val transactionService: TransactionService
) {

    @PostMapping
    fun submitTransactionAsync(@RequestBody transactionRequest: PaymentTransactionRequest) =
        CoroutineScope(transactionDispatcher).async(coroutineExceptionHandler) {
            transactionHandler.submitTransaction(transactionRequest)
        }

    @GetMapping("/{id}")
    fun getTransaction(@PathVariable id: UUID) = transactionService.findById(id)
}

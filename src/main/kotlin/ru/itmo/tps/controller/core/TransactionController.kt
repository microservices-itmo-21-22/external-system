package ru.itmo.tps.controller.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import org.springframework.web.bind.annotation.*
import ru.itmo.tps.config.coroutineExceptionHandler
import ru.itmo.tps.dto.DeliveryTransactionRequest
import ru.itmo.tps.dto.NotificationTransactionRequest
import ru.itmo.tps.dto.PaymentTransactionRequest
import ru.itmo.tps.dto.TransactionType
import ru.itmo.tps.service.core.TransactionHandler
import ru.itmo.tps.service.management.TransactionService
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/transactions")
class TransactionController(
    private val transactionHandler: TransactionHandler,
    private val transactionDispatcher: CoroutineDispatcher,
    private val transactionService: TransactionService
) {
    
    @PostMapping("/delivery")
    fun submitTransactionAsync(@RequestBody @Valid transactionRequest: DeliveryTransactionRequest) =
        CoroutineScope(transactionDispatcher).async(coroutineExceptionHandler) {
            transactionHandler.submitTransaction(transactionRequest, TransactionType.DELIVERY)
        }

    @PostMapping("/notification")
    fun submitTransactionAsync(@RequestBody @Valid transactionRequest: NotificationTransactionRequest) =
        CoroutineScope(transactionDispatcher).async(coroutineExceptionHandler) {
            transactionHandler.submitTransaction(transactionRequest, TransactionType.NOTIFICATION)
        }

    @PostMapping("/payment")
    fun submitTransactionAsync(@RequestBody @Valid transactionRequest: PaymentTransactionRequest) =
        CoroutineScope(transactionDispatcher).async(coroutineExceptionHandler) {
            transactionHandler.submitTransaction(transactionRequest, TransactionType.PAYMENT)
        }

    @GetMapping("/{id}")
    fun getTransaction(@PathVariable id: UUID) = transactionService.findById(id)
}
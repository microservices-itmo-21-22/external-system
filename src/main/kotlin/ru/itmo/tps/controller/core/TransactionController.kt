package ru.itmo.tps.controller.core

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.async.DeferredResult
import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.TransactionRequest
import ru.itmo.tps.service.core.TransactionHandler
import java.util.concurrent.ExecutorService

@RestController
@RequestMapping("transactions")
class TransactionController(
    private val transactionHandler: TransactionHandler,
    private val transactionHandlingExecutor: ExecutorService
) {
    @PostMapping
    fun submitTransaction(@RequestBody transactionRequest: TransactionRequest) : DeferredResult<Transaction> {
        val deferredResult = DeferredResult<Transaction>(500L)

        transactionHandlingExecutor.submit {
            deferredResult.setResult(transactionHandler.submitTransaction(transactionRequest))
        }

        return deferredResult
    }
}
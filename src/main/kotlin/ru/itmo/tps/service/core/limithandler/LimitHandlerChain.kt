package ru.itmo.tps.service.core.limithandler

import ru.itmo.tps.dto.Transaction

class LimitHandlerChain(private val limitHandlers: List<LimitHandler>) {

    suspend fun handle(transaction: Transaction) : Transaction {
        var handledTransaction: Transaction = transaction
        for (handler in limitHandlers) {
            handledTransaction = handler.handle(handledTransaction)
        }

        return handledTransaction
    }
}
package ru.itmo.tps.service.core.limithandler

import ru.itmo.tps.dto.Transaction

interface LimitHandler {
    suspend fun handle(transaction: Transaction): Transaction
}
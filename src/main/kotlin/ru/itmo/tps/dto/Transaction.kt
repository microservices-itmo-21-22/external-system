package ru.itmo.tps.dto

import java.time.Instant
import java.time.LocalDateTime

data class Transaction(
    val id: String,
    var status: TransactionStatus,
    val submitTime: Long = Instant.now().toEpochMilli(),
    var completedTime: LocalDateTime? = null
)
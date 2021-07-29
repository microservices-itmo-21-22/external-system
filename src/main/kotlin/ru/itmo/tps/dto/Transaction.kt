package ru.itmo.tps.dto

import java.time.LocalDateTime

data class Transaction(
    val id: String,
    var status: TransactionStatus,
    val submitTime: LocalDateTime = LocalDateTime.now(),
    var completedTime: LocalDateTime? = null
)
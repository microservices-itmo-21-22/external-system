package ru.itmo.tps.dto

import java.time.Instant

data class Transaction(
    val id: String,
    var status: TransactionStatus,
    val submitTime: Long = Instant.now().toEpochMilli(),
    var completedTime: Long? = null,
    var delta: Long? = null
)
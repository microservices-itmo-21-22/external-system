package ru.itmo.tps.dto

import java.time.LocalDateTime

data class ApiError(
    val timestamp: Long,
    val message: String
)

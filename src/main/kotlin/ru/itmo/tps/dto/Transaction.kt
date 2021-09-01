package ru.itmo.tps.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant
import java.util.*

data class Transaction(
    val id: UUID,
    val status: TransactionStatus,
    @JsonIgnore
    val accountId: UUID,
    val submitTime: Long = Instant.now().toEpochMilli(),
    val completedTime: Long? = null
) {
    val delta: Long
        get() = completedTime?.minus(submitTime) ?: 0

    fun complete() = Transaction( // todo sukhoa you can do the same by using copy method
        id = this.id,
        submitTime = this.submitTime,
        completedTime = Instant.now().toEpochMilli(),
        status = if (this.status == TransactionStatus.PENDING) TransactionStatus.SUCCESS else this.status,
        accountId = this.accountId
    )
}
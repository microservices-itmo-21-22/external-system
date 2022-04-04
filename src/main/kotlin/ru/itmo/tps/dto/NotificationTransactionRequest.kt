package ru.itmo.tps.dto

import java.util.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull

class NotificationTransactionRequest(
    clientSecret: UUID,
    @field:Email val email: String
) : TransactionRequest(clientSecret)

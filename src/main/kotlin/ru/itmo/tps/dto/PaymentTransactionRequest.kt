package ru.itmo.tps.dto

import java.util.*
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

class PaymentTransactionRequest(
    clientSecret: UUID,
    @field:Min(0) val sum: Long
) : TransactionRequest(clientSecret)

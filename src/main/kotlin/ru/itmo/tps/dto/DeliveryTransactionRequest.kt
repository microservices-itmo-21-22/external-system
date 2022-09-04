package ru.itmo.tps.dto

import java.util.*
import javax.validation.constraints.NotEmpty

class DeliveryTransactionRequest(
    clientSecret: UUID,
    @field:NotEmpty val address: String
) : TransactionRequest(clientSecret)

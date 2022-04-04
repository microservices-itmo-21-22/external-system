package ru.itmo.tps.dto

import java.util.*
import javax.validation.constraints.NotNull

abstract class TransactionRequest (
    @field:NotNull val clientSecret: UUID
)

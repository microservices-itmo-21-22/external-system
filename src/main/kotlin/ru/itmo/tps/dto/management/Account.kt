package ru.itmo.tps.dto.management

import ru.itmo.tps.entity.management.AnswerMethod
import java.util.*

data class Account (
    val id : UUID,
    val name: String,
    val answerMethod: AnswerMethod,
    val projectId: UUID,
    val callbackUrl: String?,
    val clientSecret: UUID,
    val accountLimitsId: UUID
)

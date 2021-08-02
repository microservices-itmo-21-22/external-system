package ru.itmo.tps.dto.management

import ru.itmo.tps.entity.management.AnswerMethod
import java.util.*

data class AccountCreateRequest(
    val name: String,
    val callbackUrl: String?,
    val projectId: UUID,
    val answerMethod: AnswerMethod
)

package ru.itmo.tps.dto.management

import java.util.*

data class Project (
    val id: UUID?,
    val name: String?,
    val accounts: Set<Account>?
)
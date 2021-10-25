package ru.itmo.tps.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.tps.entity.management.AccountEntity
import java.util.*

interface AccountRepository : JpaRepository<AccountEntity, UUID> {
    fun findByClientSecret(clientSecret: UUID): Optional<AccountEntity>

    fun findByAccountLimitsId(accountLimitsId: UUID): Optional<AccountEntity>
}
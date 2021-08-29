package ru.itmo.tps.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.tps.entity.management.AccountLimitsEntity
import java.util.*

interface AccountLimitsRepository : JpaRepository<AccountLimitsEntity, UUID>
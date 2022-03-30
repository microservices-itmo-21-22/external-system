package ru.itmo.tps.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.tps.entity.core.TransactionEntity
import java.util.*

interface TransactionRepository : JpaRepository<TransactionEntity, UUID>
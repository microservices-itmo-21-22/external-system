package ru.itmo.tps.service.management

import org.springframework.stereotype.Service
import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.entity.toDto
import ru.itmo.tps.entity.toEntity
import ru.itmo.tps.exception.EntityNotFoundException
import ru.itmo.tps.repository.TransactionRepository
import java.util.*

@Service
class TransactionService(private val repository: TransactionRepository) {

    fun findById(id: UUID) : Transaction = repository.findById(id).orElseThrow { EntityNotFoundException(id) }.toDto()

    fun save(transaction: Transaction) : Transaction = repository.save(transaction.toEntity()).toDto()
}
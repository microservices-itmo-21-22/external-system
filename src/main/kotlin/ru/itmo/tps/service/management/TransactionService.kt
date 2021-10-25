package ru.itmo.tps.service.management

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import ru.itmo.tps.config.coroutineExceptionHandler
import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.entity.toDto
import ru.itmo.tps.entity.toEntity
import ru.itmo.tps.exception.EntityNotFoundException
import ru.itmo.tps.repository.TransactionRepository
import java.util.*

@Service
class TransactionService(
    private val repository: TransactionRepository,
    private val databaseDispatcher: CoroutineDispatcher
) {

    fun findById(id: UUID): Transaction = repository.findById(id).orElseThrow { EntityNotFoundException(id) }.toDto()

    suspend fun save(transaction: Transaction): Job =
        CoroutineScope(databaseDispatcher).launch(coroutineExceptionHandler) {
            saveSync(transaction)
        }

    fun saveSync(transaction: Transaction): Transaction = repository.save(transaction.toEntity()).toDto()
}
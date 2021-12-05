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
import java.util.concurrent.ConcurrentHashMap

@Service
class TransactionService(
    private val repository: TransactionRepository,
    private val databaseDispatcher: CoroutineDispatcher
) {
    private val pendingTransactionCache = ConcurrentHashMap<UUID, Transaction>(5000)

    fun findById(id: UUID): Transaction {
        return pendingTransactionCache.getOrElse(id) {
            repository.findById(id).orElseThrow { EntityNotFoundException(id) }.toDto()
        }
    }

    fun cachePending(transaction: Transaction) {
        pendingTransactionCache[transaction.id] = transaction
    }

    fun evictPending(transactionId: UUID) {
        pendingTransactionCache.remove(transactionId)
    }

    suspend fun save(transaction: Transaction): Job =
        CoroutineScope(databaseDispatcher).launch(coroutineExceptionHandler) {
            saveSync(transaction)
        }

    suspend fun saveAndEvict(transaction: Transaction): Job =
        CoroutineScope(databaseDispatcher).launch(coroutineExceptionHandler) {
            saveSync(transaction)
            evictPending(transaction.id)
        }

    fun saveSync(transaction: Transaction): Transaction = repository.save(transaction.toEntity()).toDto()
}
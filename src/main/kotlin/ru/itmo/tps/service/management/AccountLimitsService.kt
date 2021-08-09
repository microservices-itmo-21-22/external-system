package ru.itmo.tps.service.management

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import ru.itmo.tps.dto.management.AccountLimits
import ru.itmo.tps.dto.management.AccountLimitsCreateRequest
import ru.itmo.tps.entity.management.AccountLimitsEntity
import ru.itmo.tps.entity.toDto
import ru.itmo.tps.entity.toEntity
import ru.itmo.tps.exception.EntityNotFoundException
import ru.itmo.tps.exception.EntityNotValidException
import ru.itmo.tps.repository.AccountLimitsRepository
import java.util.*

@Service
class AccountLimitsService(private val repository: AccountLimitsRepository) {
    @Cacheable(value = ["accountLimitsCache"], key = "#id")
    fun findById(id: UUID): AccountLimits =
        repository.findById(id).orElseThrow { EntityNotFoundException(id) }.toDto()

    fun createDefault(): AccountLimits = repository.save(
        AccountLimitsEntity(
            id = UUID.randomUUID(),
            acceptTransactions = true,
            enableResponseTimeVariation = false,
            responseTimeLowerBound = 0,
            responseTimeUpperBound = 0,
            enableFailures = false,
            failureProbability = 0.0,
            enableRateLimits = false,
            requestsPerSecond = 0,
            requestsPerMinute = 0,
            requestsPerHour = 0,
            requestsPerDay = 0,
            enableServerErrors = false,
            serverErrorProbability = 0.0
        )
    ).toDto()

    fun create(accountLimitsCreateRequest: AccountLimitsCreateRequest): AccountLimits {
        val accountLimitsEntity = AccountLimitsEntity(
            UUID.randomUUID(),
            acceptTransactions = accountLimitsCreateRequest.acceptTransactions,
            enableResponseTimeVariation = accountLimitsCreateRequest.enableResponseTimeVariation,
            responseTimeLowerBound = accountLimitsCreateRequest.responseTimeLowerBound ?: 0,
            responseTimeUpperBound = accountLimitsCreateRequest.responseTimeUpperBound ?: 0,
            enableFailures = accountLimitsCreateRequest.enableFailures,
            failureProbability = accountLimitsCreateRequest.failureProbability ?: 0.0,
            enableRateLimits = accountLimitsCreateRequest.enableRateLimits,
            requestsPerSecond = accountLimitsCreateRequest.requestsPerSecond ?: 0,
            requestsPerMinute = accountLimitsCreateRequest.requestsPerMinute ?: 0,
            requestsPerHour = accountLimitsCreateRequest.requestsPerHour ?: 0,
            requestsPerDay = accountLimitsCreateRequest.requestsPerDay ?: 0,
            enableServerErrors = accountLimitsCreateRequest.enableServerErrors,
            serverErrorProbability = accountLimitsCreateRequest.serverErrorProbability ?: 0.0
        )

        validateAndThrow(accountLimitsEntity)

        return repository.save(accountLimitsEntity).toDto()
    }

    @Caching(
        evict = [
            CacheEvict(value = ["accountLimitsCache"], key = "#id")
        ]
    )
    fun update(id: UUID, newAccountLimits: AccountLimits): AccountLimits {
        val oldEntity = repository.findById(id).orElseThrow { EntityNotFoundException(id) }
        map(newAccountLimits.toEntity(), oldEntity)

        validateAndThrow(oldEntity)

        return repository.save(oldEntity).toDto()
    }

    @CacheEvict(value = ["accountLimitsCache"], key = "#id")
    fun deleteById(id: UUID) {
        repository.deleteById(id)
    }

    fun validateAndThrow(accountLimitsEntity: AccountLimitsEntity) {
        val errors: MutableList<String> = mutableListOf()
        if (accountLimitsEntity.enableResponseTimeVariation == true) {
            if (accountLimitsEntity.responseTimeLowerBound == null) {
                errors += "Response time lower bound cannot be null when enabled variation"
            }

            if (accountLimitsEntity.responseTimeLowerBound!! < 0.0) {
                errors += "Response time lower bound cannot be negative"
            }

            if (accountLimitsEntity.responseTimeUpperBound == null) {
                errors += "Response time upper bound cannot be null when enabled variation"
            }

            if (accountLimitsEntity.responseTimeUpperBound!! < accountLimitsEntity.responseTimeLowerBound!!) {
                errors += "Response time upper bound cannot be less than lower bound"
            }
        }

        if (accountLimitsEntity.enableFailures == true) {
            if (accountLimitsEntity.failureProbability == null) {
                errors += "Failure probability cannot be null when failures enables"
            }

            if (accountLimitsEntity.failureProbability!! !in 0.0..100.0) {
                errors += "Failure probability must be in range [0.0, 100.0]"
            }
        }

        if (accountLimitsEntity.enableRateLimits == true) {
            // TODO: 07.08.2021 add validation for rate limits
        }

        if (accountLimitsEntity.enableServerErrors == true) {
            if (accountLimitsEntity.serverErrorProbability == null) {
                errors += "Server error probability cannot be null with server errors enabled"
            }

            if (accountLimitsEntity.serverErrorProbability!! !in 0.0..100.0) {
                errors += "Server error probability must be in range [0.0, 100.0]"
            }
        }

        if (errors.isNotEmpty()) {
            throw EntityNotValidException(errors.joinToString())
        }
    }

    fun map(entity: AccountLimitsEntity, targetEntity: AccountLimitsEntity) {
        targetEntity.acceptTransactions = entity.acceptTransactions
        targetEntity.enableResponseTimeVariation = entity.enableResponseTimeVariation
        targetEntity.responseTimeLowerBound = entity.responseTimeLowerBound
        targetEntity.responseTimeUpperBound = entity.responseTimeUpperBound
        targetEntity.enableFailures = entity.enableFailures
        targetEntity.failureProbability = entity.failureProbability
        targetEntity.enableRateLimits = entity.enableRateLimits
        targetEntity.requestsPerSecond = entity.requestsPerSecond
        targetEntity.requestsPerMinute = entity.requestsPerMinute
        targetEntity.requestsPerHour = entity.requestsPerHour
        targetEntity.requestsPerDay = entity.requestsPerDay
        targetEntity.enableServerErrors = entity.enableServerErrors
        targetEntity.serverErrorProbability = entity.serverErrorProbability
    }
}
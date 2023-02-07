package ru.itmo.tps.service.management

import mu.KotlinLogging
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.tps.dto.management.Account
import ru.itmo.tps.dto.management.AccountCreateRequest
import ru.itmo.tps.dto.management.AccountLimits
import ru.itmo.tps.entity.management.AccountEntity
import ru.itmo.tps.entity.management.AnswerMethod
import ru.itmo.tps.entity.toDto
import ru.itmo.tps.entity.toEntity
import ru.itmo.tps.exception.EntityNotFoundException
import ru.itmo.tps.exception.EntityNotValidException
import ru.itmo.tps.repository.AccountRepository
import ru.itmo.tps.service.core.ratelimits.RateLimitsService
import java.util.*

@Service
class AccountService(
    private val repository: AccountRepository,
    private val projectService: ProjectService,
    private val accountLimitsService: AccountLimitsService,
    private val cacheManager: CacheManager,
    private val rateLimitsService: RateLimitsService
) {

    private val log = KotlinLogging.logger {}

    fun findById(id: UUID): Account = repository.findById(id).orElseThrow { EntityNotFoundException(id) }.toDto()

    fun findAccountLimitsById(id: UUID): AccountLimits =
        repository.findById(id).orElseThrow { EntityNotFoundException(id) }.accountLimits!!.toDto()

    @Cacheable(value = ["accountCache"], key = "#clientSecret")
    fun findByClientSecret(clientSecret: UUID): Account = repository.findByClientSecret(clientSecret)
        .orElseThrow { EntityNotFoundException("Cannot find Account with client secret: $clientSecret") }
        .toDto()

    fun create(createRequest: AccountCreateRequest): Account {
        val project = projectService.findById(createRequest.projectId)

        val accountEntity = AccountEntity(
            UUID.randomUUID(),
            createRequest.name,
            createRequest.answerMethod,
            UUID.randomUUID(),
            createRequest.callbackUrl,
            project.toEntity(),
            accountLimitsService.createDefault().toEntity(),
            createRequest.transactionCost
        )

        validateAndThrow(accountEntity)

        val account = repository.save(accountEntity)
        log.info { "Account ${account.name} created, id=${account.id}" }
        return account.toDto()
    }

    @Caching(
        evict = [
            CacheEvict(value = ["accountCache"], key = "#result.clientSecret"),
            CacheEvict(value = ["projectCache"], key = "#result.projectId")
        ]
    )
    fun update(id: UUID, newAccount: Account): Account {
        val oldEntity = repository.findById(id).orElseThrow { EntityNotFoundException(id) }
        map(newAccount.toEntity(), oldEntity)

        validateAndThrow(oldEntity)

        val account = repository.save(oldEntity).toDto()
        rateLimitsService.evict(account.id)

        return account
    }

    @Transactional
    fun deleteById(id: UUID) {
        val accountOptional = repository.findById(id)
        if (accountOptional.isEmpty) return

        val accountEntity = accountOptional.get()
        cacheManager.getCache("accountCache")?.evict(accountEntity.clientSecret!!)
        cacheManager.getCache("projectCache")?.evict(accountEntity.project!!.id!!)

        accountEntity.project?.removeAccount(accountEntity)
        repository.deleteById(accountEntity.id!!)

        rateLimitsService.evict(id)
    }

    private fun validateAndThrow(accountEntity: AccountEntity) {
        if (accountEntity.answerMethod == AnswerMethod.CALLBACK && accountEntity.callbackUrl.isNullOrEmpty()) {
            throw EntityNotValidException("Callback url must be provided with AnswerMethod=Callback")
        }

        if (accountEntity.accountLimits == null) {
            throw EntityNotValidException("Account limits cannot be null")
        }

        if (accountEntity.transactionCost == null || accountEntity.transactionCost!! < 0) {
            throw EntityNotValidException("Transaction cost must be positive number")
        }
    }

    private fun map(entity: AccountEntity, targetEntity: AccountEntity) {
        targetEntity.name = entity.name
        targetEntity.callbackUrl = entity.callbackUrl
        targetEntity.clientSecret = entity.clientSecret
        targetEntity.answerMethod = entity.answerMethod
        targetEntity.transactionCost = entity.transactionCost
    }
}

package ru.itmo.tps.service.management

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import ru.itmo.tps.dto.management.Account
import ru.itmo.tps.dto.management.AccountCreateRequest
import ru.itmo.tps.entity.management.AccountEntity
import ru.itmo.tps.entity.management.AnswerMethod
import ru.itmo.tps.entity.toDto
import ru.itmo.tps.entity.toEntity
import ru.itmo.tps.exception.EntityNotFoundException
import ru.itmo.tps.exception.EntityNotValidException
import ru.itmo.tps.repository.AccountRepository
import java.util.*

@Service
class AccountService(
    private val repository: AccountRepository,
    private val projectService: ProjectService,
    private val accountLimitsService: AccountLimitsService
) {
    @Cacheable(value = ["accountCache"], key = "#id") // todo remove caching by id? is not used in transactions
    fun findById(id: UUID): Account = repository.findById(id).orElseThrow { EntityNotFoundException(id) }.toDto()

    @Caching(
        put = [CachePut(value = ["accountCache"], key = "#result.clientSecret")],
        cacheable = [Cacheable(value = ["accountCache"], key = "#clientSecret")]
    )
    fun findByClientSecret(clientSecret: UUID): Account = repository.findByClientSecret(clientSecret)
        .orElseThrow { EntityNotFoundException("Cannot find Account with client secret: $clientSecret") }
        .toDto()

    fun create(createRequest: AccountCreateRequest): Account {
        val project = projectService.findById(createRequest.projectId)

        var accountEntity = AccountEntity(
            UUID.randomUUID(),
            createRequest.name,
            createRequest.answerMethod,
            UUID.randomUUID(),
            createRequest.callbackUrl,
            project.toEntity(),
            accountLimitsService.createDefault().toEntity()
        )

        validateAndThrow(accountEntity)

        accountEntity = repository.save(accountEntity)
        return accountEntity.toDto()
    }

    @Caching(
        evict = [
            CacheEvict(value = ["accountCache"], key = "#id"),
            CacheEvict(value = ["accountCache"], key = "#result.clientSecret"),
            CacheEvict(value = ["projectCache"], key = "#result.projectId")
        ]
    )
    fun update(id: UUID, newAccount: Account): Account {
        val oldEntity = repository.findById(id).orElseThrow { EntityNotFoundException(id) }
        map(newAccount.toEntity(), oldEntity)

        validateAndThrow(oldEntity)

        return repository.save(oldEntity).toDto()
    }

    @Caching(
        evict = [
            CacheEvict(value = ["accountCache"], key = "#id"),
            CacheEvict(value = ["accountCache"], key = "#result.clientSecret"),
            CacheEvict(value = ["projectCache"], allEntries = true) // todo smarter project cache eviction
        ]
    )
    fun deleteById(id: UUID) = repository.deleteById(id)

    private fun validateAndThrow(accountEntity: AccountEntity) {
        if (accountEntity.answerMethod == AnswerMethod.CALLBACK && accountEntity.callbackUrl.isNullOrEmpty()) {
            throw EntityNotValidException("Callback url must be provided with AnswerMethod=Callback")
        }
    }

    private fun map(entity: AccountEntity, targetEntity: AccountEntity) {
        targetEntity.name = entity.name
        targetEntity.callbackUrl = entity.callbackUrl
        targetEntity.clientSecret = entity.clientSecret
        targetEntity.answerMethod = entity.answerMethod
    }
}

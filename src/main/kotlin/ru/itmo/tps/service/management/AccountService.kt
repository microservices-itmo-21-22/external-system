package ru.itmo.tps.service.management

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import ru.itmo.tps.dto.management.Account
import ru.itmo.tps.dto.management.AccountCreateRequest
import ru.itmo.tps.entity.management.AccountEntity
import ru.itmo.tps.entity.management.AnswerMethod
import ru.itmo.tps.entity.management.ProjectEntity
import ru.itmo.tps.exception.EntityNotFoundException
import ru.itmo.tps.exception.EntityNotValidException
import java.util.*

@Service
class AccountService(
    val repository: JpaRepository<AccountEntity, UUID>,
    val projectService: ProjectService
) {
    @Cacheable(value = ["accountCache"], key = "#id")
    fun findById(id: UUID): Account {
        val accountEntity = repository.findById(id).orElseThrow { EntityNotFoundException(id) }
        return toDto(accountEntity)
    }

    fun create(createRequest: AccountCreateRequest): Account {
        val project = projectService.findById(createRequest.projectId)

        var accountEntity = AccountEntity(
            UUID.randomUUID(),
            createRequest.name,
            createRequest.answerMethod,
            UUID.randomUUID(),
            createRequest.callbackUrl,
            ProjectService.toEntity(project)
        )

        validateAndThrow(accountEntity)

        accountEntity = repository.save(accountEntity)
        return toDto(accountEntity)
    }

    @CacheEvict(value = ["accountCache", "projectCache"], key = "#account.id")
    fun save(account: Account): Account {
        var accountEntity = toEntity(account)
        accountEntity = repository.save(accountEntity)
        return toDto(accountEntity)
    }

    @CacheEvict(value = ["accountCache", "projectCache"], key = "#id")
    fun update(id: UUID, newAccount: Account): Account {
        val oldEntity = repository.findById(id).orElseThrow { EntityNotFoundException(id) }
        map(toEntity(newAccount), oldEntity)

        validateAndThrow(oldEntity)

        return toDto(repository.save(oldEntity))
    }

    @CacheEvict(value = ["accountCache", "projectCache"], key = "#id")
    fun deleteById(id: UUID) {
        repository.deleteById(id)
    }

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

    companion object {
        fun toEntity(dto: Account): AccountEntity = AccountEntity(
            dto.id,
            dto.name,
            dto.answerMethod,
            dto.clientSecret,
            dto.callbackUrl,
            ProjectEntity(dto.projectId)
        )

        fun toDto(entity: AccountEntity): Account = Account(
            id = entity.id,
            name = entity.name ?: "Unknown",
            answerMethod = entity.answerMethod,
            callbackUrl = entity.callbackUrl ?: "",
            clientSecret = entity.clientSecret,
            projectId = entity.project?.id
        )
    }
}

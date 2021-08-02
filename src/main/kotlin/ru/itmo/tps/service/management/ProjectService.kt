package ru.itmo.tps.service.management

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import ru.itmo.tps.dto.management.Account
import ru.itmo.tps.dto.management.Project
import ru.itmo.tps.dto.management.ProjectCreateRequest
import ru.itmo.tps.entity.management.AccountEntity
import ru.itmo.tps.entity.management.ProjectEntity
import ru.itmo.tps.exception.EntityNotFoundException
import java.util.*
import java.util.stream.Collectors

@Service
class ProjectService(val repository: JpaRepository<ProjectEntity, UUID>) {
    @Cacheable(value = ["projectCache"], key = "#id")
    fun findById(id: UUID): Project {
        val projectEntity = repository.findById(id).orElseThrow { EntityNotFoundException(id) }
        return toDto(projectEntity)
    }

    fun create(projectCreateRequest: ProjectCreateRequest): Project {
        val projectEntity = ProjectEntity(UUID.randomUUID(), projectCreateRequest.name, mutableSetOf())

        return toDto(repository.save(projectEntity))
    }

    fun save(project: Project): Project {
        var projectEntity = toEntity(project)
        projectEntity = repository.save(projectEntity)
        return toDto(projectEntity)
    }

    @CacheEvict(value = ["projectCache"], key = "#id")
    fun update(id: UUID, newProject: Project): Project {
        val oldEntity = repository.findById(id).orElseThrow { EntityNotFoundException(id) }
        map(toEntity(newProject), oldEntity)
        return toDto(repository.save(oldEntity))
    }

    @CacheEvict(value = ["projectCache"], key = "#id")
    fun deleteById(id: UUID) {
        repository.deleteById(id)
    }

    private fun map(entity: ProjectEntity, targetEntity: ProjectEntity) {
        targetEntity.name = entity.name
    }

    companion object {
        fun toDto(entity: ProjectEntity): Project = Project(
            id = entity.id,
            name = entity.name,
            accounts = entity.accounts.map { AccountService.toDto(it) }.toMutableSet()
        )

        fun toEntity(dto: Project) : ProjectEntity = ProjectEntity(
            dto.id,
            dto.name,
            if (dto.accounts.isNullOrEmpty())
                mutableSetOf()
            else
                dto.accounts.map { AccountService.toEntity(it) }.toMutableSet()
        )
    }
}
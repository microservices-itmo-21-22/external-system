package ru.itmo.tps.service.management

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import ru.itmo.tps.dto.management.Project
import ru.itmo.tps.dto.management.ProjectCreateRequest
import ru.itmo.tps.entity.management.ProjectEntity
import ru.itmo.tps.entity.toDto
import ru.itmo.tps.entity.toEntity
import ru.itmo.tps.exception.EntityNotFoundException
import ru.itmo.tps.repository.ProjectRepository
import java.util.*

@Service
class ProjectService(private val repository: ProjectRepository) {
    @Cacheable(value = ["projectCache"], key = "#id")
    fun findById(id: UUID): Project = repository.findById(id).orElseThrow { EntityNotFoundException(id) }.toDto()

    fun create(projectCreateRequest: ProjectCreateRequest): Project {
        val projectEntity = ProjectEntity(UUID.randomUUID(), projectCreateRequest.name, mutableSetOf())

        return repository.save(projectEntity).toDto()
    }

    @CacheEvict(value = ["projectCache"], key = "#id")
    fun update(id: UUID, newProject: Project): Project {
        val oldEntity = repository.findById(id).orElseThrow { EntityNotFoundException(id) }
        map(newProject.toEntity(), oldEntity)
        return repository.save(oldEntity).toDto()
    }

    @CacheEvict(value = ["projectCache"], key = "#id")
    fun deleteById(id: UUID) = repository.deleteById(id)

    private fun map(entity: ProjectEntity, targetEntity: ProjectEntity) {
        targetEntity.name = entity.name
    }
}
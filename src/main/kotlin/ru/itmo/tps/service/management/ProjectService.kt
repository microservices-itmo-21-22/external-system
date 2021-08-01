package ru.itmo.tps.service.management

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import ru.itmo.tps.entity.management.ProjectEntity
import ru.itmo.tps.exception.EntityNotFoundException
import java.util.*

@Service
class ProjectService(val repository: JpaRepository<ProjectEntity, UUID>) {
    @Cacheable(value = ["projectCache"], key = "#id")
    fun findById(id: UUID): ProjectEntity {
        return repository.findById(id).orElseThrow { EntityNotFoundException(id) }
    }

    fun save(projectEntity: ProjectEntity): ProjectEntity {
        return repository.save(projectEntity)
    }

    @CacheEvict(value = ["projectCache"], key = "#id")
    fun update(id: UUID, projectEntity: ProjectEntity): ProjectEntity {
        val entity = findById(id)
        entity.name = projectEntity.name
        return save(entity)
    }

    @CacheEvict(value = ["projectCache"], key = "#id")
    fun deleteById(id: UUID) {
        repository.deleteById(id)
    }
}
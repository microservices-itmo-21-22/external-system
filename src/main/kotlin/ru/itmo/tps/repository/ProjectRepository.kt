package ru.itmo.tps.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.tps.entity.management.ProjectEntity
import java.util.*

interface ProjectRepository : JpaRepository<ProjectEntity, UUID>
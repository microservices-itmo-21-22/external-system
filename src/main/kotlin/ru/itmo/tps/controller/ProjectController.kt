package ru.itmo.tps.controller

import org.springframework.web.bind.annotation.*
import ru.itmo.tps.entity.management.ProjectEntity
import ru.itmo.tps.service.management.ProjectService
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(val projectService: ProjectService) {

    @GetMapping("{id}")
    fun get(@PathVariable id: UUID) : ProjectEntity {
        return projectService.findById(id)
    }

    @PostMapping
    fun create(@RequestBody project: ProjectEntity) : ProjectEntity {
        return projectService.save(project)
    }

    @PutMapping("{id}")
    fun update(@PathVariable id: UUID, @RequestBody project: ProjectEntity) : ProjectEntity {
        return projectService.update(id, project)
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: UUID) {
        projectService.deleteById(id)
    }
}
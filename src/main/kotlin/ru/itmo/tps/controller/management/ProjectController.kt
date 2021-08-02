package ru.itmo.tps.controller.management

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.*
import ru.itmo.tps.dto.management.Project
import ru.itmo.tps.dto.management.ProjectCreateRequest
import ru.itmo.tps.service.management.ProjectService
import java.util.*

@RestController
@RequestMapping("management/projects")
class ProjectController(val projectService: ProjectService) {

    @GetMapping("{id}")
    @Operation(security = [SecurityRequirement(name = "management")])
    fun get(@PathVariable id: UUID): Project {
        return projectService.findById(id)
    }

    @PostMapping
    @Operation(security = [SecurityRequirement(name = "management")])
    fun create(@RequestBody projectCreateRequest: ProjectCreateRequest): Project {
        return projectService.create(projectCreateRequest)
    }

    @PutMapping("{id}")
    @Operation(security = [SecurityRequirement(name = "management")])
    fun update(@PathVariable id: UUID, @RequestBody project: Project): Project {
        return projectService.update(id, project)
    }

    @DeleteMapping("{id}")
    @Operation(security = [SecurityRequirement(name = "management")])
    fun delete(@PathVariable id: UUID) {
        projectService.deleteById(id)
    }
}
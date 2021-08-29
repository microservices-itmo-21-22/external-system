package ru.itmo.tps.controller.management

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.*
import ru.itmo.tps.dto.management.AccountLimits
import ru.itmo.tps.dto.management.AccountLimitsCreateRequest
import ru.itmo.tps.service.management.AccountLimitsService
import java.util.*

@RestController
@RequestMapping("management/accountLimits")
class AccountLimitsController(private val accountLimitsService: AccountLimitsService) {
    @GetMapping("{id}")
    @Operation(security = [SecurityRequirement(name = "management")])
    fun get(@PathVariable id: UUID): AccountLimits {
        return accountLimitsService.findById(id)
    }

    @PostMapping
    @Operation(security = [SecurityRequirement(name = "management")])
    fun create(@RequestBody request: AccountLimitsCreateRequest): AccountLimits {
        return accountLimitsService.create(request)
    }

    @PutMapping("{id}")
    @Operation(security = [SecurityRequirement(name = "management")])
    fun update(@PathVariable id: UUID, @RequestBody accountLimits: AccountLimits): AccountLimits {
        return accountLimitsService.update(id, accountLimits)
    }

    @DeleteMapping("{id}")
    @Operation(security = [SecurityRequirement(name = "management")])
    fun delete(@PathVariable id: UUID) {
        accountLimitsService.deleteById(id)
    }
}
package ru.itmo.tps.controller.management

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.*
import ru.itmo.tps.dto.management.Account
import ru.itmo.tps.dto.management.AccountCreateRequest
import ru.itmo.tps.dto.management.AccountLimits
import ru.itmo.tps.service.management.AccountService
import java.util.*

@RestController
@RequestMapping("management/accounts")
class AccountController(private val accountService: AccountService) {
    @GetMapping("{id}")
    @Operation(security = [SecurityRequirement(name = "management")])
    fun get(@PathVariable id: UUID): Account {
        return accountService.findById(id)
    }

    @GetMapping("{id}/accountLimits")
    @Operation(security = [SecurityRequirement(name = "management")])
    fun getAccountLimits(@PathVariable id: UUID): AccountLimits {
        return accountService.findAccountLimitsById(id)
    }

    @PostMapping
    @Operation(security = [SecurityRequirement(name = "management")])
    fun create(@RequestBody request: AccountCreateRequest): Account {
        return accountService.create(request)
    }

    @PutMapping("{id}")
    @Operation(security = [SecurityRequirement(name = "management")])
    fun update(@PathVariable id: UUID, @RequestBody account: Account): Account {
        return accountService.update(id, account)
    }

    @DeleteMapping("{id}")
    @Operation(security = [SecurityRequirement(name = "management")])
    fun delete(@PathVariable id: UUID) {
        accountService.deleteById(id)
    }
}
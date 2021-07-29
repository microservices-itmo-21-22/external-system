package ru.itmo.tps.service.management

import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.tps.entity.management.AccountEntity
import java.util.*

class AccountService(repository: JpaRepository<AccountEntity, UUID>) : BaseService<AccountEntity>(repository) {



}
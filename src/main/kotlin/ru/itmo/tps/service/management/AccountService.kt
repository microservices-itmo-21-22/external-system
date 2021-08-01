package ru.itmo.tps.service.management

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import ru.itmo.tps.entity.management.AccountEntity
import java.util.*

@Service
class AccountService(repository: JpaRepository<AccountEntity, UUID>) {

}
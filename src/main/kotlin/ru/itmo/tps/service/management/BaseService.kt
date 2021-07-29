package ru.itmo.tps.service.management

import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.tps.exception.EntityNotFoundException
import java.util.*

abstract class BaseService<T> (protected val repository: JpaRepository<T, UUID>) {

    fun findById(id: UUID): T = repository.findById(id).orElseThrow { EntityNotFoundException(id) }
}
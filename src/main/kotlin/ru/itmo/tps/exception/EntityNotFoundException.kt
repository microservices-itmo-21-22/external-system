package ru.itmo.tps.exception

import java.util.*

class EntityNotFoundException(id: UUID) : Exception("Entity with id $id was not found")
package ru.itmo.tps.exception

import java.util.*

class EntityNotFoundException : Exception {
    constructor(message: String) : super(message)

    constructor(id: UUID) : super("Entity with id=$id was not found")
}
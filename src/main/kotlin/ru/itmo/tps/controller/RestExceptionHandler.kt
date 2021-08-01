package ru.itmo.tps.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import ru.itmo.tps.dto.ApiError
import ru.itmo.tps.exception.EntityNotFoundException
import java.time.Instant

@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(value = [EntityNotFoundException::class])
    fun handleEntityNotFound(exception: EntityNotFoundException): ResponseEntity<Any> {
        return ResponseEntity(composeApiError(exception.message ?: "Not found"), HttpStatus.NOT_FOUND)
    }

    private fun composeApiError(message: String): ApiError {
        return ApiError(Instant.now().epochSecond, message)
    }
}
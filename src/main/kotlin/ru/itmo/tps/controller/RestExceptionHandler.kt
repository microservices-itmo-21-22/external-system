package ru.itmo.tps.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.context.request.async.AsyncRequestTimeoutException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import ru.itmo.tps.dto.ApiError
import ru.itmo.tps.exception.EntityNotFoundException
import ru.itmo.tps.exception.EntityNotValidException
import java.time.Instant

@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(value = [EntityNotFoundException::class])
    fun handleEntityNotFound(exception: EntityNotFoundException): ResponseEntity<Any> {
        return ResponseEntity(composeApiError(exception.message ?: "Not found"), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(value = [EntityNotValidException::class])
    fun handleEntityNotValid(exception: EntityNotValidException): ResponseEntity<Any> {
        return ResponseEntity(composeApiError(exception.message ?: "Bad request"), HttpStatus.BAD_REQUEST)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        return ResponseEntity(composeApiError(ex.message), HttpStatus.BAD_REQUEST)
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        return ResponseEntity(composeApiError(ex.message ?: "Cannot serialize body"), HttpStatus.BAD_REQUEST)
    }

    override fun handleAsyncRequestTimeoutException(
        ex: AsyncRequestTimeoutException,
        headers: HttpHeaders,
        status: HttpStatus,
        webRequest: WebRequest
    ): ResponseEntity<Any>? {
        return ResponseEntity(composeApiError("Timeout while submitting transaction"), HttpStatus.BAD_GATEWAY)
    }

    private fun composeApiError(message: String): ApiError {
        return ApiError(Instant.now().epochSecond, message)
    }
}
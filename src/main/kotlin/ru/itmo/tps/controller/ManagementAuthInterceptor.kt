package ru.itmo.tps.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class ManagementAuthInterceptor : HandlerInterceptor {
    @Value("\${auth.token}")
    private lateinit var token: String

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (!request.requestURI.startsWith("/management/")) {
            return true
        }

        if (header != token) {
            response.status = HttpStatus.UNAUTHORIZED.value()
            return false
        }

        return true
    }
}
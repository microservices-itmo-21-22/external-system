package ru.itmo.tps.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import ru.itmo.tps.controller.ManagementAuthInterceptor

@Configuration
class MvcConfig(val managementAuthInterceptor: ManagementAuthInterceptor) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(managementAuthInterceptor)
    }
}
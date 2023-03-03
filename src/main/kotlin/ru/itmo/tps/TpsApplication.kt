package ru.itmo.tps

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching



@OpenAPIDefinition(
    servers = [Server(url = "/", description="Your host")]
)
@SpringBootApplication
@EnableCaching
class TpsApplication

fun main(args: Array<String>) {
    runApplication<TpsApplication>(*args)
}

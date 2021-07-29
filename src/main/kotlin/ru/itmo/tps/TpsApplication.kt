package ru.itmo.tps

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class TpsApplication

fun main(args: Array<String>) {
    runApplication<TpsApplication>(*args)
}

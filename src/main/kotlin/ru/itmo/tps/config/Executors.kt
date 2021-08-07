package ru.itmo.tps.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

@Configuration
class Executors {
    @Configuration
    class TransactionHandling {
        @Value("\${executors.transaction-handling.queue-size}")
        var queueSize: Int = 5000

        @Value("\${executors.transaction-handling.threads-min}")
        var threadsMin: Int = 1

        @Value("\${executors.transaction-handling.threads-max}")
        var threadsMax: Int = 8

        @Value("\${executors.transaction-handling.keep-threads-seconds}")
        var keepThreadsSeconds: Long = 60

        @Bean
        fun transactionHandlingExecutor() = ThreadPoolExecutor(
            threadsMin,
            threadsMax,
            keepThreadsSeconds,
            TimeUnit.SECONDS,
            LinkedBlockingQueue(queueSize)
        )
    }
}
package ru.itmo.tps.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ExecutorsConfig {
    @Bean
    fun transactionHandlingExecutor() = ExecutorsFactory.threadPoolExecutor("main")
    
    @Bean
    fun postponedTransactionHandlingExecutor() = ExecutorsFactory.scheduledPoolExecutor("scheduled")
}
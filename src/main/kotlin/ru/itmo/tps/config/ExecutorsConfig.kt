package ru.itmo.tps.config

import kotlinx.coroutines.asCoroutineDispatcher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ExecutorsConfig {
    @Bean
    fun transactionDispatcher() =
        ExecutorsFactory.ExecutorBean(ExecutorsFactory.threadPoolExecutor("transactionsDispatcher"))
            .asCoroutineDispatcher()

    @Bean
    fun nonblockingTransactionDispatcher() =
        ExecutorsFactory.ExecutorBean(ExecutorsFactory.threadPoolExecutor("nonblockingTransactionsDispatcher"))
            .asCoroutineDispatcher()

    @Bean
    fun databaseDispatcher() =
        ExecutorsFactory.ExecutorBean(ExecutorsFactory.threadPoolExecutor("databaseDispatcher"))
            .asCoroutineDispatcher()
}
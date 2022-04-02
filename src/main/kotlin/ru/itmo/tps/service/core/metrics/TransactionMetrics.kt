package ru.itmo.tps.service.core.metrics

import com.oripwk.micrometer.kotlin.coTimer
import io.micrometer.core.instrument.*
import org.springframework.context.annotation.Configuration

@Configuration
class TransactionMetrics(private val registry: MeterRegistry) {

    private val ACCOUNT_NAME_TAG = "account_name"

    private val spentMoneyCounter =
        createCounter("money.spent", "Money spent for external system transactions")

    private val failedTransactionsCounter =
        createCounter("transactions.failed", "Failed transactions")

    private val successfulTransactionsCounter =
        createCounter("transactions.successful", "Successful transactions")

    private val errorTransactionsCounter =
        createCounter("transactions.errors", "Transaction processing errors")

    fun countSpentMoney(accountName: String, value: Double) = spentMoneyCounter.inc(accountName, value)

    fun countFailedTransaction(accountName: String) = failedTransactionsCounter.inc(accountName)

    fun countSuccessfulTransaction(accountName: String) = successfulTransactionsCounter.inc(accountName)

    fun countTransactionError(accountName: String) = errorTransactionsCounter.inc(accountName)

    suspend fun <T> executeTransactionTimed(accountName: String, action: suspend () -> T): T = Timer.builder("transactions.execution_time")
        .coTimer(
            meterRegistry = registry,
            description = "Transaction execution time",
            tags = listOf(Tag.of(ACCOUNT_NAME_TAG, accountName))
        ).record(action)


    private fun createCounter(name: String, description: String) = Counter.builder(name)
        .description(description)
        .tag(ACCOUNT_NAME_TAG, "")
        .register(registry)

    private fun Counter.inc(accountName: String, amount: Double = 1.0) =
        Metrics.counter(this.id.name, ACCOUNT_NAME_TAG, accountName).increment(amount)
}
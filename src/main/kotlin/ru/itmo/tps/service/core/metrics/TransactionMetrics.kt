package ru.itmo.tps.service.core.metrics

import com.oripwk.micrometer.kotlin.coTimer
import io.micrometer.core.instrument.*
import io.micrometer.core.instrument.Timer
import org.springframework.context.annotation.Configuration
import ru.itmo.tps.dto.TransactionType
import java.util.*

@Configuration
class TransactionMetrics(private val registry: MeterRegistry) {

    private val ACCOUNT_NAME_TAG = "account_name"
    private val TRANSACTION_TYPE_TAG = "transaction_type"

    private val spentMoneyCounter =
        createCounter("money.spent", "Money spent for external system transactions")

    private val failedTransactionsCounter =
        createCounter("transactions.failed", "Failed transactions")

    private val successfulTransactionsCounter =
        createCounter("transactions.successful", "Successful transactions")

    private val errorTransactionsCounter =
        createCounter("transactions.errors", "Transaction processing errors")

    fun countSpentMoney(
        transactionType: TransactionType,
        accountName: String,
        value: Double
    ) = spentMoneyCounter.inc(transactionType, accountName, value)

    fun countFailedTransaction(
        transactionType: TransactionType,
        accountName: String
    ) = failedTransactionsCounter.inc(transactionType, accountName)

    fun countSuccessfulTransaction(
        transactionType: TransactionType,
        accountName: String
    ) = successfulTransactionsCounter.inc(transactionType, accountName)

    fun countTransactionError(
        transactionType: TransactionType,
        accountName: String
    ) = errorTransactionsCounter.inc(transactionType, accountName)

    suspend fun <T> executeTransactionTimed(
        transactionType: TransactionType,
        accountName: String,
        action: suspend () -> T
    ): T =
        Timer.builder("transactions.execution_time")
            .coTimer(
                meterRegistry = registry,
                description = "Transaction execution time",
                tags = listOf(
                    Tag.of(ACCOUNT_NAME_TAG, accountName),
                    Tag.of(TRANSACTION_TYPE_TAG, transactionType.toString())
                )
            ).record(action)


    private fun createCounter(name: String, description: String) = Counter.builder(name)
        .description(description)
        .tag(ACCOUNT_NAME_TAG, "")
        .tag(TRANSACTION_TYPE_TAG, "")
        .register(registry)

    private fun Counter.inc(
        transactionType: TransactionType,
        accountName: String,
        amount: Double = 1.0
    ) =
        Metrics.counter(
            this.id.name,
            ACCOUNT_NAME_TAG, accountName,
            TRANSACTION_TYPE_TAG, transactionType.toString()
        ).increment(amount)
}
package ru.itmo.tps.service.core.handlestrategy

import org.springframework.stereotype.Service
import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.TransactionStatus
import ru.itmo.tps.dto.management.Account
import ru.itmo.tps.dto.management.AccountLimits
import ru.itmo.tps.entity.management.AnswerMethod
import java.time.Instant
import kotlin.random.Random
import kotlin.random.nextInt

@Service
class BlockingTransactionHandlingStrategy : TransactionHandlingStrategy {
    override fun supports(account: Account): Boolean = account.answerMethod == AnswerMethod.TRANSACTION


    override fun handle(transaction: Transaction, accountLimits: AccountLimits) {
        val sleepMillis = Random.nextInt(0..5000).toLong()

        Thread.sleep(sleepMillis)

        transaction.status = TransactionStatus.SUCCESS
        transaction.completedTime = Instant.now().toEpochMilli()
        transaction.delta = transaction.completedTime!! - transaction.submitTime
    }
}
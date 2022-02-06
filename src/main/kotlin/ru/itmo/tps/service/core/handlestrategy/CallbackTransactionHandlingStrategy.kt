package ru.itmo.tps.service.core.handlestrategy

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import ru.itmo.tps.config.WebClientConfig.Companion.RETRY_COUNT
import ru.itmo.tps.config.coroutineExceptionHandler
import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.TransactionStatus
import ru.itmo.tps.dto.management.Account
import ru.itmo.tps.entity.management.AnswerMethod
import ru.itmo.tps.exception.TransactionSubmittingFailureException
import ru.itmo.tps.service.core.limithandler.LimitHandlerChainBuilder
import ru.itmo.tps.service.core.limithandler.impl.ServerErrorsLimitHandler
import ru.itmo.tps.service.core.ratelimits.RateLimitsService
import ru.itmo.tps.service.management.TransactionService

@Service
class CallbackTransactionHandlingStrategy(private val nonblockingTransactionDispatcher: CoroutineDispatcher,
    private val transactionService: TransactionService,
    private val webClient: WebClient,
    private val rateLimitsService: RateLimitsService
) : TransactionHandlingStrategy {
    private val logger = KotlinLogging.logger {}

    override fun supports(account: Account) = AnswerMethod.CALLBACK == account.answerMethod

    override suspend fun handle(transaction: Transaction, account: Account): Transaction {
        rateLimitsService.acquire(account)
        try {
            ServerErrorsLimitHandler.create(account.accountLimits).handle(transaction)
        } catch (e: TransactionSubmittingFailureException) {
            rateLimitsService.release(account)
            throw e
        }

        val limitHandlerChainBuilder = LimitHandlerChainBuilder(account.accountLimits)
        limitHandlerChainBuilder.enableResponseTimeVariation()
        limitHandlerChainBuilder.enableTransactionFailure()

        CoroutineScope(nonblockingTransactionDispatcher).launch(coroutineExceptionHandler) {
            val handledTransaction = limitHandlerChainBuilder.build()
                .handle(transaction)
                .complete(account.transactionCost)

            transactionService.save(handledTransaction)

            if (shouldCallback(account, handledTransaction)) {
                doCallback(account.callbackUrl!!, handledTransaction)
            }

            rateLimitsService.release(account)
        }

        return transaction
    }

    private fun shouldCallback(account: Account, transaction: Transaction): Boolean =
        !(account.accountLimits.failureLostTransaction && transaction.status == TransactionStatus.FAILURE)

    suspend fun doCallback(callbackUrl: String, transaction: Transaction) {
        val callbackMono = webClient
            .post()
            .uri(callbackUrl).bodyValue(transaction)
            .exchangeToMono { response ->
                if (!response.statusCode().isError) {
                    response.toBodilessEntity()
                } else {
                    response.createException().flatMap {
                        logger.error { "Error during callback request $it" }
                        Mono.error<ResponseEntity<Void>>(it)
                    }
                }
            }
            .retry(RETRY_COUNT)

        callbackMono.subscribe()
    }
}
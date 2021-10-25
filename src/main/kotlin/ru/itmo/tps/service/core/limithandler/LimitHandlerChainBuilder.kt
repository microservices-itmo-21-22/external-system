package ru.itmo.tps.service.core.limithandler

import ru.itmo.tps.dto.management.AccountLimits
import ru.itmo.tps.service.core.limithandler.impl.ResponseTimeVariationLimitHandler
import ru.itmo.tps.service.core.limithandler.impl.ServerErrorsLimitHandler
import ru.itmo.tps.service.core.limithandler.impl.TransactionFailureLimitHandler

class LimitHandlerChainBuilder(val accountLimits: AccountLimits) {
    private val handlers: MutableList<LimitHandler> = mutableListOf()

    fun enableResponseTimeVariation(): LimitHandlerChainBuilder {
        handlers.add(ResponseTimeVariationLimitHandler.create(accountLimits))
        return this
    }

    fun enableTransactionFailure(): LimitHandlerChainBuilder {
        handlers.add(TransactionFailureLimitHandler.create(accountLimits))
        return this
    }

    fun enableServerErrors(): LimitHandlerChainBuilder {
        handlers.add(ServerErrorsLimitHandler.create(accountLimits))
        return this
    }

    fun build(): LimitHandlerChain = LimitHandlerChain(handlers)
}
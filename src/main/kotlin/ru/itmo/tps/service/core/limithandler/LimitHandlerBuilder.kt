package ru.itmo.tps.service.core.limithandler

import ru.itmo.tps.dto.management.AccountLimits
import ru.itmo.tps.service.core.limithandler.impl.NoLimitationsLimitHandler
import ru.itmo.tps.service.core.limithandler.impl.ResponseTimeVariationLimitHandler
import ru.itmo.tps.service.core.limithandler.impl.ServerErrorsLimitHandler
import ru.itmo.tps.service.core.limithandler.impl.TransactionFailureLimitHandler

class LimitHandlerBuilder(val accountLimits: AccountLimits) {
    var limitHandler: LimitHandler = NoLimitationsLimitHandler()

    fun enableResponseTimeVariation(): LimitHandlerBuilder {
        limitHandler = ResponseTimeVariationLimitHandler.create(limitHandler, accountLimits)
        return this
    }

    fun enableTransactionFailure(): LimitHandlerBuilder {
        limitHandler = TransactionFailureLimitHandler.create(limitHandler, accountLimits)
        return this
    }

    fun enableServerErrors(): LimitHandlerBuilder {
        limitHandler = ServerErrorsLimitHandler.create(limitHandler, accountLimits)
        return this
    }

    fun build(): LimitHandler = limitHandler
}
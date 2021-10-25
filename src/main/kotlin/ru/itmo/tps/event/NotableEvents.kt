package ru.itmo.tps.event

import com.itmo.microservices.commonlib.logging.NotableEvent

enum class NotableEvents(private val template: String): NotableEvent {
    E_TRANSACTION_SUBMISSION_ERROR("Could not submit transaction with id={}"),
    I_TRANSACTION_SUCCEEDED("Transaction succeeded, id={}"),
    I_TRANSACTION_FAILURE("Transaction failed, id={}"),

    ;

    override fun getName(): String {
        return name
    }

    override fun getTemplate(): String {
        return template
    }
}
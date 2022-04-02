package ru.itmo.tps.dto

import java.util.*

class PaymentTransactionRequest(clientSecret: UUID, val sum: Long) : TransactionRequest(clientSecret)
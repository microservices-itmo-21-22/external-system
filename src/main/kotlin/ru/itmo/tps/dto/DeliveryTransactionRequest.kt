package ru.itmo.tps.dto

import java.util.*

class DeliveryTransactionRequest(clientSecret: UUID, val address: String) : TransactionRequest(clientSecret)

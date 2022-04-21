package com.itmo.microservices.demo.payment.api.model

import java.util.*

data class PaymentLogRecord(
    val timestamp: Long?,
    val status: PaymentStatus?,
    val amount: Int?,
    val transactionId: UUID?
)

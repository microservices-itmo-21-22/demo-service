package com.itmo.microservices.demo.payment.api.model

import com.itmo.microservices.demo.order.api.model.PaymentStatus
import java.util.*

data class PaymentLogRecord(
    val timestamp: Long,
    val status: PaymentStatus,
    val amount: Int,
    val transactionId: UUID
)

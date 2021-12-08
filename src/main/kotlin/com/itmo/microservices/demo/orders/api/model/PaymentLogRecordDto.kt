package com.itmo.microservices.demo.orders.api.model

import com.itmo.microservices.demo.orders.impl.entity.PaymentStatus
import java.util.*

data class PaymentLogRecordDto (
    val timestamp: Long,
    val status: PaymentStatus,
    val amount: Long,
    val transactionId: UUID
)
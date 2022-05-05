package com.itmo.microservices.demo.order.api.model

import com.itmo.microservices.demo.order.common.PaymentStatus
import java.util.*

data class PaymentLogRecordDto (
    val timestamp: Long?,
    val status: PaymentStatus?,
    val amount: Int?,
    val transactionId: UUID?
)
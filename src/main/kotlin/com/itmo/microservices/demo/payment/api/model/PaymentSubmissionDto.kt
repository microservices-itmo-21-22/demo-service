package com.itmo.microservices.demo.payment.api.model

import java.util.*

data class PaymentSubmissionDto (
    val timestamp: Long,
    val transactionId: UUID
)

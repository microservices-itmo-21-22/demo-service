package com.itmo.microservices.demo.payment.api.model

import java.util.*

class PaymentSubmission (
    val timestamp: Long,
    val transactionId: UUID
)
package com.itmo.microservices.demo.payment.api.model

import java.util.*

class PaymentSubmissionDto {
    var timestamp: Long = 0
    var transactionId: UUID = UUID.randomUUID()
}
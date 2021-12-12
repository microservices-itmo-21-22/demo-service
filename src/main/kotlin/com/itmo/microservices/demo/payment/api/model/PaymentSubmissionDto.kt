package com.itmo.microservices.demo.payment.api.model

import java.sql.Timestamp
import java.util.UUID

class PaymentSubmissionDto(var timestamp: Long, var transactionId: UUID) {

}
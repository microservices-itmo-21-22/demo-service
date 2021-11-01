package com.itmo.microservices.demo.payment.api.model

import java.util.*

data class PaymentLogRecordDto(val timestamp: Long, val paymentStatus: PaymentStatus, val amount: Int, val transactionId: UUID)
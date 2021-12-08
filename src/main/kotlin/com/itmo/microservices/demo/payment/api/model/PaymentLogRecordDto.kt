package com.itmo.microservices.demo.payment.api.model

import java.util.UUID
import com.itmo.microservices.demo.payment.api.util.PaymentStatus

class PaymentLogRecordDto(var timestamp: Long, var status: PaymentStatus, var amount: Int, var transactionId: UUID) {

}

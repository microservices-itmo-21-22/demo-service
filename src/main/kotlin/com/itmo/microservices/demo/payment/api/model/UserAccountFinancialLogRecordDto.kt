package com.itmo.microservices.demo.payment.api.model

import java.util.UUID
import com.itmo.microservices.demo.payment.api.util.FinancialOperationType
import java.time.temporal.TemporalAmount

class UserAccountFinancialLogRecordDto(var type: FinancialOperationType, var amount: Int, var orderId: UUID, var paymentTransactionId: UUID, var timestamp: Long) {

}
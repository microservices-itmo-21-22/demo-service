package com.itmo.microservices.demo.payments.api.model

import java.util.*

data class UserFinancialLogRecordDto(
        val type: FinancialOperationType,
        val amount: Int,
        val orderId: UUID,
        val paymentTransactionId: UUID,
        val timestamp: Long
)
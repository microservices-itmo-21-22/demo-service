package com.itmo.microservices.demo.payments.api.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.sql.Timestamp
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserAccountFinancialLogRecordDto(
    val type: FinancialOperationType?,
    val amount: Int?,
    val orderId: UUID?,
    val paymentTransactionId: UUID?,
    val timestamp: Long?
)
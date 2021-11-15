package com.itmo.microservices.demo.payments.api.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PaymentLogRecordDto (
    val transactionId: UUID?,
    val timestamp: Long?,
    val status: PaymentStatus?,
    val amount: Int?
)
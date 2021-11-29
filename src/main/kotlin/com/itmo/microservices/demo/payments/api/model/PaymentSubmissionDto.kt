package com.itmo.microservices.demo.payments.api.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PaymentSubmissionDto(
    val transactionId: UUID?,
    val timestamp: Long?
)

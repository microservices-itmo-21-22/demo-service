package com.itmo.microservices.demo.payments.api.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TransactionDto (
        val id: UUID?,
        val status: TransactionStatus?,
        val submitTime: Long?,
        val completedTime: Long?,
        val cost: Int?,
        val delta: Int?
)
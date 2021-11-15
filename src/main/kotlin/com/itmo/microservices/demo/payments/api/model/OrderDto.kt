package com.itmo.microservices.demo.payments.api.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*


@JsonInclude(JsonInclude.Include.NON_NULL)
data class OrderDto (
    val id: UUID?,
    val timeCreated: Long?,
    val status: OrderStatus?,
    val itemsMap: Map<UUID, Int>?,
    val deliveryDuration: Int?,
    val paymentHistory: List<PaymentLogRecordDto>?
)
package com.itmo.microservices.demo.orderService.api.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

data class OrderDto (
    val id: UUID,
    val timeCreated: Long,
    val status : OrderStatus,
    val itemsMap : Map <UUID, Int>,
    val deliveryDuration : Int?,
    val paymentHistory : List<PaymentLogRecord>
)
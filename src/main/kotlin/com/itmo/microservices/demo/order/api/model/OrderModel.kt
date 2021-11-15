package com.itmo.microservices.demo.order.api.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.itmo.microservices.demo.order.impl.entity.OrderItem
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OrderModel (
        val id: UUID?,
        val timeCreated: Long?,
        val status: OrderStatus?,
        val itemsMap: Map<OrderItem, Int>?,
        val deliveryDuration: Int?,
        val paymentHistory: List<PaymentLogRecord>?
)
package com.itmo.microservices.demo.order.api.model

import com.itmo.microservices.demo.items.api.model.OrderItem
import java.util.*

data class OrderDto(
    val id: UUID,
    val userId: UUID,
    val timeCreated: Long,
    val status: OrderStatus,
    val itemsMap: Map<OrderItem, Int>,
    val deliveryDuration: Int,
    val paymentHistory: List<PaymentLogRecordDto>
)
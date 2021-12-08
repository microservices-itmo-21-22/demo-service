package com.itmo.microservices.demo.orders.api.model

import com.itmo.microservices.demo.orders.impl.entity.OrderStatus
import com.itmo.microservices.demo.orders.impl.entity.PaymentLogRecord
import java.util.*

data class OrderDto (
    val id: UUID,
    val timeCreated: Long,
    val status: OrderStatus,
    val itemsMap: Map<UUID, Long>,
    val deliveryDuration: Int?,
    val paymentHistory: List<PaymentLogRecord>
)
package com.itmo.microservices.demo.order.api.model

import com.itmo.microservices.demo.order.common.OrderStatus
import java.util.*
import kotlin.collections.HashMap

data class OrderModel(
    val id: UUID?,
    val timeCreated: Long?,
    val status: OrderStatus?,
    val itemsMap: Map<UUID?, Int?>,
    val deliveryDuration: Int?,
    val paymentHistory: List<PaymentLogRecordDto>?
    )
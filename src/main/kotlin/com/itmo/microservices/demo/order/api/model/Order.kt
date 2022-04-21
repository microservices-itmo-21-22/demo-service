package com.itmo.microservices.demo.order.api.model

import com.itmo.microservices.demo.payment.impl.entity.PaymentLogRecordEntity
import java.util.*

data class Order(
    val id: UUID?,
    val timeCreated: Long?,
    val status: OrderStatus?,
    val itemsMap: Map<UUID, Int>?,
    val deliveryDuration: Int?,
    val paymentHistory: List<PaymentLogRecordEntity>
)
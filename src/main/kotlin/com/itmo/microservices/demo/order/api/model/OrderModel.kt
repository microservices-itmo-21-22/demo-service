package com.itmo.microservices.demo.order.api.model

import com.itmo.microservices.demo.order.impl.entity.OrderStatus
import com.itmo.microservices.demo.order.impl.entity.PaymentLogRecord
import java.util.*

data class OrderModel(
    val timeCreated: Long,
    val status: OrderStatus,
    var itemsMap: Map<UUID, Int>,
    var deliveryDuration: Int?,
    var paymentHistory: List<PaymentLogRecord>)
{
    //fun OrderDetails(): OrderDto = OrderDto(timeCreated, status, deliveryDuration)
}
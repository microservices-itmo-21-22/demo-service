package com.itmo.microservices.demo.order.api.model

import java.util.*
import javax.persistence.OneToMany

data class OrderModel(
    val id: UUID,
    val timeCreated: Long,
    val status: OrderStatus,
    @OneToMany
    var itemsMap: List<ItemMap>,
    var deliveryDuration: Int?//,
    //var paymentHistory: List<PaymentLogRecord>
    )
{
    //fun OrderDetails(): OrderDto = OrderDto(timeCreated, status, deliveryDuration)
}
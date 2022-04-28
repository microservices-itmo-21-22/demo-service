package com.itmo.microservices.demo.order.api.model

import java.util.*

data class OrderItemModel(
    val id: UUID,
    val title: String,
    val price: Int
) {
    //fun OrderItemDetails(): OrderItemDto
}
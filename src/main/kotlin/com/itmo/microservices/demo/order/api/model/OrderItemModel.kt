package com.itmo.microservices.demo.order.api.model

data class OrderItemModel(
    val title: String,
    val price: Int
) {
    //fun OrderItemDetails(): OrderItemDto
}
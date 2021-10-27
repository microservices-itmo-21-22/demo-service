package com.itmo.microservices.demo.items.impl.util

import com.itmo.microservices.demo.items.api.model.OrderItem
import com.itmo.microservices.demo.items.impl.entity.OrderItemEntity

fun OrderItem.toEntity(): OrderItemEntity =  OrderItemEntity(
    id = this.id,
    title = this.title,
    price = this.price
)

fun OrderItemEntity.toModel(): OrderItem = OrderItem(
    id = this.id,
    title = this.title,
    price = this.price
)
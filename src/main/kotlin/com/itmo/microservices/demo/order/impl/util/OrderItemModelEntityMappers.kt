package com.itmo.microservices.demo.items.impl.util

import com.itmo.microservices.demo.order.api.model.OrderItemDto
import com.itmo.microservices.demo.order.impl.entities.OrderItem
import com.itmo.microservices.demo.order.impl.entities.Order

fun OrderItemDto.toEntity(amountFromOrderDto: Int?, orderFromOrderDto: Order?): OrderItem = OrderItem(
    id = this.id,
    title = this.title,
    price = this.price,
    amount = amountFromOrderDto,
    order = orderFromOrderDto
)

fun OrderItem.toModel(): OrderItemDto = OrderItemDto(
    id = this.id,
    title = this.title,
    price = this.price
)
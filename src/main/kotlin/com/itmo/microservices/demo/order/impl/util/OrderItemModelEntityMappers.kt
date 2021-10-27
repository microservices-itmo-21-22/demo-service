package com.itmo.microservices.demo.items.impl.util

import com.itmo.microservices.demo.order.api.model.OrderItemDto
import com.itmo.microservices.demo.order.impl.entities.OrderItem
import com.itmo.microservices.demo.order.impl.entities.OrderEntity

fun OrderItemDto.toEntity(amountFromOrderDto: Int?, orderFromOrderEntityDto: OrderEntity?): OrderItem = OrderItem(
    id = this.id,
    title = this.title,
    price = this.price,
    amount = amountFromOrderDto,
    orderEntity = orderFromOrderEntityDto
)

fun OrderItem.toModel(): OrderItemDto = OrderItemDto(
    id = this.id,
    title = this.title,
    price = this.price
)

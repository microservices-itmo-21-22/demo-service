package com.itmo.microservices.demo.lib.common.order.mappers

import com.itmo.microservices.demo.lib.common.order.dto.OrderItemDto
import com.itmo.microservices.demo.lib.common.order.entities.OrderItemEntity
import com.itmo.microservices.demo.lib.common.order.entities.OrderEntity

fun OrderItemDto.toEntity(amountFromOrderDto: Int?, orderFromOrderEntityDto: OrderEntity?): OrderItemEntity = OrderItemEntity(
    id = this.id,
    title = this.title,
    price = this.price,
    amount = amountFromOrderDto,
    orderEntity = orderFromOrderEntityDto
)

fun OrderItemEntity.toModel(): OrderItemDto = OrderItemDto(
    id = this.id,
    title = this.title,
    price = this.price
)

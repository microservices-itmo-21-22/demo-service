package com.itmo.microservices.demo.order.impl.util

import com.itmo.microservices.demo.order.api.model.OrderDto
import com.itmo.microservices.demo.order.api.model.OrderStatus
import com.itmo.microservices.demo.order.api.model.PaymentLogRecordDto
import com.itmo.microservices.demo.order.impl.entity.Amount
import com.itmo.microservices.demo.order.impl.entity.OrderEntity
import java.util.*

fun OrderEntity.toModel(): OrderDto = OrderDto(
    id = this.id,
    timeCreated = this.timeCreated,
    status = this.status,
    itemsMap = this.itemsMap?.mapValues { it.value.amount ?: 0 },
    deliveryDuration = this.deliveryDuration,
    paymentHistory = this.paymentHistory?.map { it.toModel() }
)

fun OrderDto.toEntity(username: String?): OrderEntity = OrderEntity(
        username,
        this.timeCreated,
        this.status,
        this.itemsMap?.mapValues { Amount(it.value) }?.toMutableMap(),
        this.deliveryDuration,
        this.paymentHistory?.map { it.toEntity() }
)
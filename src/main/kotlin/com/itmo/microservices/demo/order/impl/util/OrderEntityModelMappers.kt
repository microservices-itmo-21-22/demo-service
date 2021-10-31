package com.itmo.microservices.demo.order.impl.util

import com.itmo.microservices.demo.order.api.model.OrderModel
import com.itmo.microservices.demo.order.impl.entity.OrderEntity

fun OrderEntity.toModel(): OrderModel = OrderModel(
        id = this.id,
        date = this.date,
        busket = this.busket?.id
)
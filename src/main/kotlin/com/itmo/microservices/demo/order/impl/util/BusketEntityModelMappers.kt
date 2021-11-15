package com.itmo.microservices.demo.order.impl.util

import com.itmo.microservices.demo.order.api.model.BusketModel
import com.itmo.microservices.demo.order.impl.entity.Busket

fun Busket.toModel(): BusketModel = BusketModel(
        id = this.id,
        products = this.items?.mapNotNull { it.id } ?: listOf(),
        user = this.username,
        order = this.order?.id
)

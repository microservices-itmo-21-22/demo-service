package com.itmo.microservices.demo.delivery.impl.util

import com.itmo.microservices.demo.delivery.api.model.DeliveryModel
import com.itmo.microservices.demo.delivery.impl.entity.Delivery

fun DeliveryModel.toEntity() : Delivery = Delivery(
    id = this.id,
    orderId = this.orderId,
    address = this.address
)

fun Delivery.toModel(): DeliveryModel = DeliveryModel(
    id = this.id,
    orderId = this.orderId,
    address = this.address,
    slot = this.slot
)
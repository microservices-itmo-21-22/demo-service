package com.itmo.microservices.demo.delivery.impl.util

import com.itmo.microservices.demo.delivery.api.model.DeliveryModel
import com.itmo.microservices.demo.delivery.impl.entity.Delivery

fun DeliveryModel.toEntity() : Delivery = Delivery(
    id = this.id,
    address = this.address,
    date = this.date
)

fun Delivery.toModel(): DeliveryModel = DeliveryModel(
    id = this.id,
    address = this.address,
    date = this.date
)
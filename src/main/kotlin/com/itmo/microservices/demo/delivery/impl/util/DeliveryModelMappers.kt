package com.itmo.microservices.demo.delivery.impl.util

import com.itmo.microservices.demo.delivery.api.model.DeliveryModel
import com.itmo.microservices.demo.delivery.impl.entity.Delivery

fun Delivery.toModel(): DeliveryModel = kotlin.runCatching {
    DeliveryModel(
        id = this.id!!,
        type = this.type!!,
        warehouse = this.warehouse!!,
        deliveryDuration = this.deliveryDuration!!,
        address = this.address!!,
        courierCompany = this.courierCompany!!
    )
}.getOrElse { exception -> throw IllegalStateException("Some of delivery fields are null", exception) }
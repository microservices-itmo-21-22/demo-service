package com.itmo.microservices.demo.delivery.api.model

import com.itmo.microservices.demo.delivery.impl.entity.DeliveryPayment
import java.util.*

data class DeliveryModel (
    val id: UUID?,
    val date: Date?,
    val address: String?,
    val cost: Double?,
    val payment: DeliveryPayment
)
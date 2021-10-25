package com.itmo.microservices.demo.delivery.api.model

import com.itmo.microservices.demo.delivery.impl.entity.Delivery
import com.itmo.microservices.demo.delivery.impl.entity.DeliveryAppUser
import java.util.*

data class PaymentModel (
    val id: UUID?,
    val date: Date?,
    val status: Int?,
    val delivery: Delivery?,
    val user: DeliveryAppUser?
)
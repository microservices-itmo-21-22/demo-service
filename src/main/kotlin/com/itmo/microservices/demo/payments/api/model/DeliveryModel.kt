package com.itmo.microservices.demo.payments.api.model

import com.itmo.microservices.demo.payments.impl.entity.Payment
import java.util.*

data class DeliveryModel (
        val id: UUID?,
        val date: Date?,
        val address: String?,
        val cost: Double?,
        val payment: Payment
)
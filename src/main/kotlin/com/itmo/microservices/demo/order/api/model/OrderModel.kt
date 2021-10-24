package com.itmo.microservices.demo.order.api.model

import com.itmo.microservices.demo.order.impl.entity.Busket
import java.util.*

data class OrderModel (
        val id: UUID?,
        val date: Date?,
        val busket: Busket?
)
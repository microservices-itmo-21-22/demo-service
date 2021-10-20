package com.itmo.microservices.demo.orders.api.model

import java.util.*

data class OrderModel (
    val id: UUID,
    val basketId: UUID,
    val date: Date?
) {}

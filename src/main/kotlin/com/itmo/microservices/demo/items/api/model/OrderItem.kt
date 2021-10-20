package com.itmo.microservices.demo.items.api.model

import java.util.*

data class OrderItem (
    val id: UUID,
    val title: String,
    val price: Int
)
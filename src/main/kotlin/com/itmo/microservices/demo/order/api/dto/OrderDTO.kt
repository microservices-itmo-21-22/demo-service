package com.itmo.microservices.demo.order.api.dto

import java.util.*

data class OrderDTO (
    val id: UUID?,
    val date: Date?,
    val busketId: UUID?
)
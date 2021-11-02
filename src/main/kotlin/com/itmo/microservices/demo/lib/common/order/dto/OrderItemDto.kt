package com.itmo.microservices.demo.lib.common.order.dto

import java.util.*

data class OrderItemDto (
    val id: UUID?,
    val title: String?,
    val price: String?
)

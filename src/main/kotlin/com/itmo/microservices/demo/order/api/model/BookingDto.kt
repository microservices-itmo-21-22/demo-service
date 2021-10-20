package com.itmo.microservices.demo.order.api.model

import java.util.*

data class BookingDto (
    val id: UUID,
    val failedItems: Set<UUID>,
    val orderId: UUID
)
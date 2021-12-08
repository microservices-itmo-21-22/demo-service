package com.itmo.microservices.demo.orders.api.model

import java.util.*

data class BookingDto (
    val id: UUID,
    val failedItems: Set<UUID>
    )
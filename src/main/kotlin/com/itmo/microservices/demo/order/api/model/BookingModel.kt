package com.itmo.microservices.demo.order.api.model

import java.util.*

data class BookingModel (
    val id: UUID,
    val failedItems: Set<UUID>
        ) {
}
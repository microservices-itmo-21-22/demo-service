package com.itmo.microservices.demo.order.api.model

import java.util.*

data class BookingModel (
    val failedItems: Set<UUID>
        ) {
}
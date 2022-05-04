package com.itmo.microservices.demo.delivery.api.model

import java.util.*

data class BookingDto (
    var id: UUID?,
    var failedItems: Set<UUID>
)

package com.itmo.microservices.demo.order.api.dto

import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Data
import lombok.NoArgsConstructor
import java.util.*

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Booking {
    private val id: UUID? = null
    private val failedItems: Set<UUID>? = null
}
package com.itmo.microservices.demo.delivery.api.model

import java.time.LocalDateTime
import java.util.*

data class DeliveryDTO (val type: DeliveryType,
                        val warehouse: Int,
                        val preferredDeliveryTime: LocalDateTime,
                        var address: String
                        )
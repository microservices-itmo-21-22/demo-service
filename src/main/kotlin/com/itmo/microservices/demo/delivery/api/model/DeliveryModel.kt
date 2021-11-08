package com.itmo.microservices.demo.delivery.api.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DeliveryModel(
    var id: UUID?,
    var user: String?,
    var type: DeliveryType?,
    var warehouse: Int?,
    var preferredDeliveryTime: LocalDateTime?,
    var address: String?,
    var courierCompany: String?
)

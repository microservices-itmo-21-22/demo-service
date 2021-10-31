package com.itmo.microservices.demo.delivery.api.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DeliveryModel(
    var id: UUID,
    var type: DeliveryType?,
    var warehouse: Int?,
    var deliveryDuration: Int?,
    var address: String?,
    var courierCompany: String?
)

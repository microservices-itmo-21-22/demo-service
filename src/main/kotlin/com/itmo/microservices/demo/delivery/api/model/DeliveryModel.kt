package com.itmo.microservices.demo.delivery.api.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.itmo.microservices.demo.tasks.api.model.TaskStatus
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DeliveryModel(
    val id: UUID?,
    val address: String?,
    val date: Date?)
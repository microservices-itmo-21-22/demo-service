package com.itmo.microservices.demo.order.api.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BusketDto (
        val id: UUID?,
        val products: List<UUID>,
        val user: String?,
        val order: UUID?
)
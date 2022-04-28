package com.itmo.microservices.demo.warehouse.api.model
import java.util.*
import java.util.UUID.randomUUID

data class CatalogItemDto(
    val id: UUID = randomUUID(),
    val title: String = "",
    val description: String = "",
    val price: Int = 100,
    val amount: Int = 0
)

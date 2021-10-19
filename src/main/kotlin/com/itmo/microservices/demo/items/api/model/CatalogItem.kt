package com.itmo.microservices.demo.items.api.model

import java.util.*

data class CatalogItem(
    val id: UUID,
    val title: String,
    val description: String,
    val price: Int = 100,
    val amount: Int
)

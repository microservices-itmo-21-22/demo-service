package com.itmo.microservices.demo.items.api.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CatalogItem(
        val title: String,
        val description: String,
        val price: Int = 100,
        val amount: Int
)

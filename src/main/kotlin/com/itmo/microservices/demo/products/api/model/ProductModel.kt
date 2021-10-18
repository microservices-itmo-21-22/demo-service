package com.itmo.microservices.demo.products.api.model;

import java.util.*

data class ProductModel (
        var id: UUID?,
        var name: String?,
        var description: String?,
        var country: String?,
        var price: Double?,
        var type: ProductType = ProductType.OTHER
)

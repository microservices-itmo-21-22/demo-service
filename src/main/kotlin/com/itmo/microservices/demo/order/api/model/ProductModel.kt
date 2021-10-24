package com.itmo.microservices.demo.order.api.model

import java.util.*

data class ProductModel (
        var id: UUID?,
        var name: String?,
        var description: String?,
        var country: String?,
        var price: Double?,
        var sale: Double?,
        var type: ProductType = ProductType.OTHER
)
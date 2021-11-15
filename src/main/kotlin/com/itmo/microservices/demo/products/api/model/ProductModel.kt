package com.itmo.microservices.demo.products.api.model

import java.util.*

data class ProductModel (
        var id: UUID?,
        var name: String?,
        var description: String?,
        var price: Int?,
        var amount:Int?

)

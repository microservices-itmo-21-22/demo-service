package com.itmo.microservices.demo.shoppingCartService.api.model

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import java.util.*

class CatalogItemDTO {

    @Type(type = "uuid-char")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    var id: UUID = UUID.randomUUID()
    var productId: UUID = UUID.randomUUID()
    var amount: Int? = 0


    constructor()

    constructor(id: UUID, productId: UUID, amount: Int?) {
        this.id = id;
        this.productId = productId
        this.amount = amount
    }
}
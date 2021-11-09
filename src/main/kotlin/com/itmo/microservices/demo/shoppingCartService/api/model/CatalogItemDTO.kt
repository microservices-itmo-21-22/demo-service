package com.itmo.microservices.demo.shoppingCartService.api.dto

import java.util.*

class CatalogItemDTO {

    var id: UUID = UUID.randomUUID()
    var productId: UUID = UUID.randomUUID()
    var amount: Int = 0


    constructor()

    constructor(id: UUID, productId: UUID, amount: Int) {
        this.id = id;
        this.productId = productId
        this.amount = amount
    }
}
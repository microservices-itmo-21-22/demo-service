package com.itmo.microservices.demo.shoppingCartService.api.dto

import java.util.*

class ShoppingCartDTO {

    var id : UUID = UUID.randomUUID()
    var status: String = "ACTIVE"
    var items: List<CatalogItemDTO> = listOf()


    constructor()

    constructor(id: UUID, status: String, items: List<CatalogItemDTO>) {
        this.id = id
        this.status = status
        this.items = items
    }
}
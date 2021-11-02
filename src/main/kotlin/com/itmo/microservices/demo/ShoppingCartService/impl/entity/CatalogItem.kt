package com.itmo.microservices.demo.ShoppingCartService.impl.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class CatalogItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    lateinit var id: UUID
    var productId: UUID = UUID.randomUUID()
    var amount: Int = 0


    constructor()

    constructor(productId: UUID, amount: Int) {
        this.productId = productId
        this.amount = amount
    }
}
package com.itmo.microservices.demo.shoppingCartService.impl.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name="CatalogItem304")
class CatalogItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    lateinit var id: UUID
    var productId: UUID = UUID.randomUUID()
    var amount: Int? = 0


    constructor()

    constructor(id : UUID, productId: UUID, amount: Int?) {
        this.id = id
        this.productId = productId
        this.amount = amount
    }
}
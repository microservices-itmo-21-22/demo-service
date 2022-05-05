package com.itmo.microservices.demo.order.impl.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "order_items")
class OrderItemEntity {

    @Id
    @GeneratedValue
    var id: UUID? = null
    var title: String? = null
    var price: Int? = null

    constructor()

    constructor(id: UUID, title: String, price: Int) {
        this.id = id
        this.title = title
        this.price = price
    }
}
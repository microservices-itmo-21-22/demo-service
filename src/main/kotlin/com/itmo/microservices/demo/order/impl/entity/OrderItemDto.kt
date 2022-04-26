package com.itmo.microservices.demo.order.impl.entity

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class OrderItemDto {

    @Id
    var title: String? = null
    var price: Int? = null

    constructor()

    constructor(title: String, price: Int) {
        this.title = title
        this.price = price
    }
}
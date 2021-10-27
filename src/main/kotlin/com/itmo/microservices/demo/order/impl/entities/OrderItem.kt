package com.itmo.microservices.demo.order.impl.entities

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
class OrderItem {
    @Id
    @GeneratedValue
    var id: UUID? = null
    var title: String? = null
    var price: Int? = null
    var amount: Int? = null
    @ManyToOne
    var orderEntity: OrderEntity? = null

    constructor()

    constructor(
        id: UUID?,
        title: String?,
        price: Int?,
        amount: Int?,
        orderEntity: OrderEntity?
    ) {
        this.id = id
        this.title = title
        this.price = price
        this.amount = amount
        this.orderEntity = orderEntity
    }
}

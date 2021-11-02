package com.itmo.microservices.demo.lib.common.order.entities

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "order_items")
class OrderItemEntity {
    @Id
    @GeneratedValue
    var id: UUID? = null
    var title: String? = null
    var price: String? = null
    var amount: Int? = null
    @ManyToOne
    var orderEntity: OrderEntity? = null

    constructor()

    constructor(
        id: UUID?,
        title: String?,
        price: String?,
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

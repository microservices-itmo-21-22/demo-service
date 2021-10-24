package com.itmo.microservices.demo.order.impl.entity

import java.util.*
import javax.persistence.*

@Entity
class Busket {

    @Id
    @GeneratedValue
    var id: UUID? = null
    @OneToMany
    var products: List<OrderProduct>? = null
    @ManyToOne
    var user: OrderAppUser? = null
    @OneToOne
    var order: OrderEntity? = null

    constructor()

    constructor(products: List<OrderProduct>?, user: OrderAppUser?) {
        this.products = products
        this.user = user
        this.order = order
    }

    override fun toString(): String {
        return "Busket(products=$products, user=$user, order=$order)"
    }


}
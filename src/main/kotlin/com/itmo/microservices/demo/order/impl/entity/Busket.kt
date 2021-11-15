package com.itmo.microservices.demo.order.impl.entity

import java.util.*
import javax.persistence.*

@Entity
class Busket {

    @Id
    @GeneratedValue
    var id: UUID? = null

    var username: String? = null

    @ManyToMany
    var items: MutableList<OrderItem>? = null

    @OneToOne
    var order: OrderEntity? = null

    constructor()

    constructor(username: String?, items: MutableList<OrderItem>?) {
        this.username = username
        this.items = items
    }

    override fun toString(): String {
        return "Busket(products=$items, user=$username, order=$order)"
    }
}
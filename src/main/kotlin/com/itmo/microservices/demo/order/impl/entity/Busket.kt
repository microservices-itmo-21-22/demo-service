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
    @JoinTable
        (
        name = "order_product",
        joinColumns = [JoinColumn(
            name = "busket_id"
        )],
        inverseJoinColumns = [JoinColumn(
            name = "order_product_id"
        )]
    )
    var products: MutableList<OrderProduct>? = null



    @OneToOne
    var order: OrderEntity? = null

    constructor()

    constructor(username: String?, products: MutableList<OrderProduct>?) {
        this.username = username
        this.products = products
    }

    override fun toString(): String {
        return "Busket(products=$products, user=$username, order=$order)"
    }
}
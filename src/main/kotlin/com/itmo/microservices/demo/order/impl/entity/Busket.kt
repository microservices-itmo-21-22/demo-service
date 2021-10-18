package com.itmo.microservices.demo.order.impl.entity

import com.itmo.microservices.demo.products.impl.entity.Product
import com.itmo.microservices.demo.users.impl.entity.AppUser
import java.util.*
import javax.persistence.*

@Entity
class Busket {

    @Id
    @GeneratedValue
    var id: UUID? = null
    @OneToMany
    var products: List<Product>? = null
    @ManyToOne
    var user: AppUser? = null
    @OneToOne
    var order: Order? = null

    constructor()

    constructor(products: List<Product>?, user: AppUser?, order: Order?) {
        this.products = products
        this.user = user
        this.order = order
    }

    override fun toString(): String {
        return "Busket(products=$products, user=$user, order=$order)"
    }


}
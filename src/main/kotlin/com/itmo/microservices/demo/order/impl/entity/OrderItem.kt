package com.itmo.microservices.demo.order.impl.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class OrderItem {
    @Id
    @GeneratedValue
    var id: UUID? = null
    var title: String? = null
    var description: String? = null
    var price: Int? = null
    var amount: Int?=null

    constructor()

//    constructor(title: String?, price: Int?) {
//        this.title = title
//        this.price = price
//    }

    constructor(title: String?, description: String?, price: Int?, amount: Int?) {
        this.title = title
        this.description = description
        this.price = price
        this.amount = amount
    }

    constructor(id: UUID?, title: String?, description: String?, price: Int?, amount: Int?) {
        println("Create orderItem with Id: $id")
        this.id = id
        this.title = title
        this.description = description
        this.price = price
        this.amount = amount
    }

    override fun toString(): String {
        return "OrderItem(id=$id, title=$title, price=$price)"
    }

}
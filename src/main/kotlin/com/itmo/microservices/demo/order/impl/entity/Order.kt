package com.itmo.microservices.demo.order.impl.entity

import com.itmo.microservices.demo.order.impl.entity.Busket
import com.itmo.microservices.demo.users.impl.entity.AppUser
import java.util.*
import javax.persistence.*

@Entity
class Order {

    @Id
    @GeneratedValue
    var id: UUID? = null
    var date: Date? = null
    @OneToOne
    var busket: Busket? = null

    constructor()

    constructor(date: Date?, busket: Busket?) {
        this.date = date
        this.busket = busket
    }

    override fun toString(): String {
        return "Order(date=$date, busket=$busket)"
    }


}
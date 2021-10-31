package com.itmo.microservices.demo.order.impl.entity

import java.util.*
import javax.persistence.*

@Entity
class OrderEntity {

    @Id
    @GeneratedValue
    var id: UUID? = null
    var date: Date? = null
    @OneToOne
    var busket: Busket? = null

    constructor()

    constructor(date: Date?) {
        this.date = date
    }

    override fun toString(): String {
        return "Order(date=$date, busket=$busket)"
    }

}
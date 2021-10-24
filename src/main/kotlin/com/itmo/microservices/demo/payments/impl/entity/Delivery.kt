package com.itmo.microservices.demo.payments.impl.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToOne

@Entity
class Delivery {

    @Id
    @GeneratedValue
    var id: UUID? = null
    var date: Date? = null
    var address: String? = null
    var cost: Double? = null
    @OneToOne
    var payment: Payment? = null

    constructor()

    constructor(date: Date?, address: String?, cost: Double?, payment: Payment?) {
        this.date = date
        this.address = address
        this.cost = cost
        this.payment = payment
    }

    override fun toString(): String {
        return "Delivery(date=$date, address=$address, cost=$cost, payment=$payment)"
    }


}
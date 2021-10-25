package com.itmo.microservices.demo.delivery.impl.entity

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
    var payment: DeliveryPayment? = null

    constructor()

    constructor(date: Date?, address: String?, cost: Double?, payment: DeliveryPayment?) {
        this.date = date
        this.address = address
        this.cost = cost
        this.payment = payment
    }

    override fun toString(): String {
        return "Delivery(date=$date, address=$address, cost=$cost, payment=$payment)"
    }
}
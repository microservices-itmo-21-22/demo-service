package com.itmo.microservices.demo.delivery.impl.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToOne

@Entity
class Delivery {
    @Id
    var id: UUID? = UUID.randomUUID()
    var date: Date? = null
    var address: String? = null
    var cost: Double? = null
    var paymentId: UUID? = null

    constructor()

    constructor(date: Date?, address: String?, cost: Double?, paymentId: UUID?) {
        this.date = date
        this.address = address
        this.cost = cost
        this.paymentId = paymentId
    }

    override fun toString(): String {
        return "Delivery(date=$date, address=$address, cost=$cost, payment=$paymentId)"
    }
}
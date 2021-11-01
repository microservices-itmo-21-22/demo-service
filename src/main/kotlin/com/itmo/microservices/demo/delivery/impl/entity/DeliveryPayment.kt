package com.itmo.microservices.demo.delivery.impl.entity

import java.util.*
import javax.persistence.*

@Entity
class DeliveryPayment {
    @Id
    @GeneratedValue
    var id: UUID? = null
    var date: Date? = null
    var status: Int? = null
    var deliveryId: UUID? = null
    var username: String? = null

    constructor()

    constructor(date: Date?, status: Int?, deliveryId: UUID?, username: String?) {
        this.date = date
        this.status = status
        this.deliveryId = deliveryId
        this.username = username
    }

    override fun toString(): String {
        return "Payment(date=$date, status=$status, delivery=$deliveryId, user=$username)"
    }
}
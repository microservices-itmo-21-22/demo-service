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

    @OneToOne
    var delivery: Delivery? = null

    @ManyToOne
    var user: DeliveryAppUser? = null

    constructor()

    constructor(date: Date?, status: Int?, delivery: Delivery?, user: DeliveryAppUser?) {
        this.date = date
        this.status = status
        this.delivery = delivery
        this.user = user
    }

    override fun toString(): String {
        return "Payment(date=$date, status=$status, delivery=$delivery, user=$user)"
    }
}
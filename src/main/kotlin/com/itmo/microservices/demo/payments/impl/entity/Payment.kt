package com.itmo.microservices.demo.payments.impl.entity

import java.util.*
import javax.persistence.*

@Entity
class Payment {

    @Id
    @GeneratedValue
    var id: UUID? = null
    var date: Date? = null
    var status: Int? = null
    @OneToOne
    var delivery: Delivery? = null
    @ManyToOne
    var user: PaymentAppUser? = null

    constructor()

    constructor(date: Date?, status: Int?, delivery: Delivery?, user: PaymentAppUser?) {
        this.date = date
        this.status = status
        this.delivery = delivery
        this.user = user
    }

    override fun toString(): String {
        return "Payment(date=$date, status=$status, delivery=$delivery, user=$user)"
    }

}
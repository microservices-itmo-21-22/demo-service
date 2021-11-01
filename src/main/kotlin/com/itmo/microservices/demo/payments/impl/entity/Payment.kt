package com.itmo.microservices.demo.payments.impl.entity

import java.util.*
import javax.persistence.*

@Entity
class Payment {

    @Id
    @GeneratedValue
    var id: UUID? = null
    var date: Date? = null
    var status: Int? = 0
    var username: String? = null

    constructor()

    constructor(date: Date?, status: Int?, username: String?) {
        this.date = date
        this.status = status
        this.username = username
    }

    override fun toString(): String {
        return "Payment(date=$date, status=$status, user=$username)"
    }

}
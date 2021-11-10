package com.itmo.microservices.demo.delivery.impl.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Delivery {
    @Id
    var id : UUID? = null
    var orderId : UUID? = null
    var address : String? = null
    var date : Date? = null

    constructor()

    constructor(id : UUID?, orderId : UUID?, address: String?, date : Date?) {
        this.id = id
        this.orderId = orderId
        this.address = address
        this.date = date
    }

}
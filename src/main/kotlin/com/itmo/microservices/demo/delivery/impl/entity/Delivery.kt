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
    var slot : Int? = null

    constructor()

    constructor(id : UUID?, orderId : UUID?, address: String?) {
        this.id = id
        this.orderId = orderId
        this.address = address
    }

    constructor(id : UUID?, orderId : UUID?, address: String?, slot: Int?) {
        this.id = id
        this.orderId = orderId
        this.address = address
        this.slot = slot
    }

}
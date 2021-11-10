package com.itmo.microservices.demo.delivery.impl.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : UUID? = null
    var address : String? = null
    var date : Date? = null

    constructor()

    constructor(id : UUID?, address: String?, date : Date?) {
        this.id = id
        this.address = address
        this.date = date
    }

}
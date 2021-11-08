package com.itmo.microservices.demo.orders.impl.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : UUID? = null
    var status : Int = 0
    var basketId : UUID? = null
    var userName : String? = null
    var date : Date? = null

    constructor()

    constructor(id : UUID?, status : Int, basketId : UUID?, userName : String?, date : Date?) {
        this.id = id
        this.status = status
        this.basketId = basketId
        this.userName = userName
        this.date = date
    }

}
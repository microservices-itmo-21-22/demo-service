package com.itmo.microservices.demo.orders.impl.entity

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "order304")
class Order {
    @Id
    @Type(type = "uuid-char")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    var id : UUID? = null
    var status : Int = 0
    var basketId : UUID? = null
    var userId : UUID? = null
    var date : Date? = null

    constructor()

    constructor(id : UUID?, status : Int, basketId : UUID?, userId : UUID?, date : Date?) {
        this.id = id
        this.status = status
        this.basketId = basketId
        this.userId = userId
        this.date = date
    }

}
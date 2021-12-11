package com.itmo.microservices.demo.orders.impl.entity

import com.itmo.microservices.demo.orders.api.model.OrderStatus
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "order")
class Order {
    @Id
    @Type(type = "uuid-char")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    var id : UUID? = null
    var status : OrderStatus = OrderStatus.COLLECTING
    var basketId : UUID? = null
    var userId : UUID? = null
    var date : Date? = null

    constructor()

    constructor(id : UUID?, status : OrderStatus, basketId : UUID?, userId : UUID?, date : Date?) {
        this.id = id
        this.status = status
        this.basketId = basketId
        this.userId = userId
        this.date = date
    }

}
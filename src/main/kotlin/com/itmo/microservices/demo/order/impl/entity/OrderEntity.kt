package com.itmo.microservices.demo.order.impl.entity


import com.itmo.microservices.demo.order.api.model.OrderStatus
import java.util.*
import javax.persistence.*

@Entity
class Amount {

    @Id
    @GeneratedValue
    var id: UUID? = null

    var amount: Int? = null

    constructor()

    constructor(amount: Int?) {
        this.amount = amount
    }

}


@Entity
class OrderEntity {

    @Id
    @GeneratedValue
    var id: UUID? = null

    var timeCreated: Long? = null

    var status: OrderStatus? = null

    @ManyToMany
    var itemsMap: MutableMap<UUID, Amount>? = null

    var deliveryDuration: Int? = null

    @OneToMany
    var paymentHistory: List<PaymentLogRecord>? = null

    constructor()

    constructor(timeCreated: Long?, status: OrderStatus?, itemsMap: MutableMap<UUID, Amount>?, deliveryDuration: Int?, paymentHistory: List<PaymentLogRecord>?) {
        this.timeCreated = timeCreated
        this.status = status
        this.itemsMap = itemsMap
        this.deliveryDuration = deliveryDuration
        this.paymentHistory = paymentHistory
    }


}
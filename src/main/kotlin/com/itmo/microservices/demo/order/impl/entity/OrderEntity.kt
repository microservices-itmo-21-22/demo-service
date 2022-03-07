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

    var username: String? = null

    var timeCreated: Long? = null

    var timeUpdated: Long? = null

    var status: OrderStatus? = null

    @ManyToMany(cascade= [CascadeType.ALL])
    var itemsMap: MutableMap<UUID, Amount>? = null

    var deliveryDuration: Int? = null

    @OneToMany
    var paymentHistory: List<PaymentLogRecordEntity>? = null

    constructor()

    constructor(username: String?, timeCreated: Long?, status: OrderStatus?, itemsMap: MutableMap<UUID, Amount>?, deliveryDuration: Int?, paymentHistory: List<PaymentLogRecordEntity>?) {
        this.username = username
        this.timeCreated = timeCreated
        this.status = status
        this.itemsMap = itemsMap
        this.deliveryDuration = deliveryDuration
        this.paymentHistory = paymentHistory
    }


}
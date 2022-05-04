package com.itmo.microservices.demo.order.impl.entity

import com.itmo.microservices.demo.order.api.model.ItemMap
import com.itmo.microservices.demo.order.api.model.OrderStatus
import com.itmo.microservices.demo.payment.api.model.PaymentLogRecord
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "orders")
class OrderEntity {

    @Id
    @GeneratedValue
    var id: UUID? = null
    var timeCreated: Long? = null
    var status: OrderStatus? = null
    @OneToMany
    var itemsMap: List<ItemMap> = emptyList()
    var deliveryDuration: Int? = null
    @OneToMany
    var paymentHistory: List<PaymentLogRecord>? = emptyList()

    constructor()

    constructor(id: UUID?,
                timeCreated: Long?,
                status: OrderStatus?,
                itemsMap: List<ItemMap>,
                deliveryDuration: Int?,
                paymentHistory: List<PaymentLogRecord>) {
        this.id = id
        this.timeCreated = timeCreated
        this.status = status
        this.itemsMap = itemsMap
        this.deliveryDuration = deliveryDuration
        this.paymentHistory = paymentHistory
    }
}
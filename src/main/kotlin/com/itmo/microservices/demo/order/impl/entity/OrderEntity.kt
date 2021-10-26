package com.itmo.microservices.demo.order.impl.entity

import com.itmo.microservices.demo.items.api.model.OrderItem
import com.itmo.microservices.demo.order.api.model.OrderStatus
import com.itmo.microservices.demo.payment.api.model.PaymentLogRecordDto
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class OrderEntity {

    @Id
    var id: UUID? = null
    var userId: UUID? = null
    var timeCreated: Long = 0
    var status: OrderStatus? = null
    //var itemsMap: Map<OrderItem, Int>? = null
    var deliveryDuration: Int = 0
    //var paymentHistory: List<PaymentLogRecordDto>? = null

    constructor()

    constructor(id: UUID?,
                userId: UUID?,
                timeCreated: Long,
                status: OrderStatus?,
                itemsMap: Map<OrderItem, Int>?,
                deliveryDuration: Int,
                paymentHistory: List<PaymentLogRecordDto>?) {
        this.id = id
        this.userId = userId
        this.timeCreated = timeCreated
        this.status = status
        this.deliveryDuration = deliveryDuration
    }
}
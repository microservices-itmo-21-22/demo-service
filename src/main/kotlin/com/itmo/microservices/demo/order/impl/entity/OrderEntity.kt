package com.itmo.microservices.demo.order.impl.entity

import com.itmo.microservices.demo.items.api.model.OrderItem
import com.itmo.microservices.demo.order.api.model.OrderStatus
import com.itmo.microservices.demo.payment.api.model.PaymentLogRecordDto
import lombok.Builder
import java.util.*
import javax.persistence.*

@Builder
@Entity
class OrderEntity {

    @Id
    var id: UUID? = null
    var userId: UUID? = null
    var timeCreated: Long = 0
    var status: OrderStatus? = null
    var deliveryDuration: Int? = 0

    @ElementCollection
    var itemsMap: Map<UUID, Int>? = null

    @ElementCollection
    var paymentHistory: List<UUID>? = null

    constructor()

    constructor(id: UUID?,
                userId: UUID?,
                timeCreated: Long,
                status: OrderStatus?,
                itemsMap: Map<UUID, Int>?,
                deliveryDuration: Int?,
                paymentHistory: List<UUID>?) {
        this.id = id
        this.userId = userId
        this.timeCreated = timeCreated
        this.status = status
        this.itemsMap = itemsMap
        this.deliveryDuration = deliveryDuration
        this.paymentHistory = paymentHistory
    }
}
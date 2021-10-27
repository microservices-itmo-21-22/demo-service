package com.itmo.microservices.demo.order.impl.entity

import com.itmo.microservices.demo.items.api.model.OrderItem
import com.itmo.microservices.demo.items.impl.entity.OrderItemEntity
import com.itmo.microservices.demo.order.api.model.OrderStatus
import com.itmo.microservices.demo.payment.api.model.PaymentLogRecordDto
import com.itmo.microservices.demo.payment.impl.entity.PaymentLogRecordEntity
import java.util.*
import javax.persistence.*

@Entity
class OrderEntity {

    @Id
    var id: UUID = UUID.randomUUID()
    var userId: UUID = UUID.randomUUID()
    var timeCreated: Long = 0
    var status: OrderStatus = OrderStatus.BOOKED
    @ElementCollection
    var itemsMap: Map<OrderItemEntity, Int> = mapOf()
    var deliveryDuration: Int = 0
    @OneToMany
    var paymentHistory: List<PaymentLogRecordEntity> = listOf()

    constructor()

    constructor(id: UUID,
                userId: UUID,
                timeCreated: Long,
                status: OrderStatus,
                itemsMap: Map<OrderItem, Int>,
                deliveryDuration: Int,
                paymentHistory: List<PaymentLogRecordDto>) {
        this.id = id
        this.userId = userId
        this.timeCreated = timeCreated
        this.status = status
        this.deliveryDuration = deliveryDuration
    }
}
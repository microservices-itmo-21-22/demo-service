package com.itmo.microservices.demo.lib.common.order.entity

import com.itmo.microservices.demo.lib.common.order.dto.OrderStatusEnum
import com.itmo.microservices.demo.payment.impl.model.PaymentLogRecordEntity
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "orders")
class OrderEntity {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false)
    var id: UUID? = null

    @Column(columnDefinition = "uuid")
    var userId: UUID? = null

    var timeCreated: LocalDateTime = LocalDateTime.now()

    @Enumerated(EnumType.STRING)
    var status: OrderStatusEnum = OrderStatusEnum.COLLECTING

    var deliveryDuration: Int? = 0

    @OneToMany
    var itemsMap: List<OrderItemEntity>? = null

    @OneToMany
    var paymentHistory: MutableList<PaymentLogRecordEntity>? = null

    constructor()

    constructor(
        id: UUID?,
        userId: UUID?,
        timeCreated: LocalDateTime,
        status: OrderStatusEnum,
        itemsMap: List<OrderItemEntity>?,
        deliveryDuration: Int?,
        paymentHistory: MutableList<PaymentLogRecordEntity>?
    ) {
        this.id = id
        this.userId = userId
        this.timeCreated = timeCreated
        this.status = status
        this.itemsMap = itemsMap
        this.deliveryDuration = deliveryDuration
        this.paymentHistory = paymentHistory
    }
}

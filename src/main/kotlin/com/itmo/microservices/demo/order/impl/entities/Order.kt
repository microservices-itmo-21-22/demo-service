package com.itmo.microservices.demo.order.impl.entities

import com.itmo.microservices.demo.order.api.model.OrderStatus
import com.itmo.microservices.demo.payment.impl.entities.UserAccountFinancialLogRecord
import lombok.Builder
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Builder
@Entity
class Order {
    @Id
    @Type(type = "uuid-char")
    @GeneratedValue(generator = "UUID")
    var id: UUID? = null
    @Type(type = "uuid-char")
    var userId: UUID? = null
    var timeCreated: Long = 0
    var status: OrderStatus = OrderStatus.COLLECTING
    var deliveryDuration: Int? = 0

    @OneToMany
    var itemsMap: List<OrderItem>? = null

    @OneToMany
    var paymentHistory: List<UserAccountFinancialLogRecord>? = null

    constructor()

    constructor(
        id: UUID?,
        userId: UUID?,
        timeCreated: Long,
        status: OrderStatus,
        itemsMap: List<OrderItem>?,
        deliveryDuration: Int?,
        paymentHistory: List<UserAccountFinancialLogRecord>?
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
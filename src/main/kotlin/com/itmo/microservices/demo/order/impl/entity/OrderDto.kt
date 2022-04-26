package com.itmo.microservices.demo.order.impl.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class OrderDto {
    @Id
    var timeCreated: Long = 0
    var status: OrderStatus? = null
    var itemsMap: Map<UUID, Int> = emptyMap<UUID, Int>()
    var deliveryDuration: Int? = null
    var paymentHistory: List<PaymentLogRecord> = emptyList<PaymentLogRecord>()

    constructor() {}

    constructor(timeCreated: Long, status: OrderStatus, deliveryDuration : Int?) {
        this.timeCreated = timeCreated
        this.status = status
        this.deliveryDuration = deliveryDuration
    }
}
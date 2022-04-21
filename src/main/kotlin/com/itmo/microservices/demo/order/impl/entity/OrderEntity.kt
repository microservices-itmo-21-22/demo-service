package com.itmo.microservices.demo.warehouse.impl.entity

import com.itmo.microservices.demo.order.api.model.OrderStatus
import com.itmo.microservices.demo.payment.impl.entity.PaymentLogRecordEntity
import java.util.*
import javax.persistence.*

@Entity
class OrderEntity {
    @Id
    @GeneratedValue
    var id: UUID? = null
    var timeCreated: Long? = null
    var status: OrderStatus? = null

    @ElementCollection
    var itemsMap: Map<UUID, Int>? = null
    var deliveryDuration: Int? = null

    @OneToMany(mappedBy = "order")
    var paymentHistory: List<PaymentLogRecordEntity> = emptyList()
}

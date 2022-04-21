package com.itmo.microservices.demo.payment.impl.entity

import com.itmo.microservices.demo.order.api.model.PaymentStatus
import com.itmo.microservices.demo.warehouse.impl.entity.OrderEntity
import java.util.*
import javax.persistence.*

@Entity
class PaymentLogRecordEntity {
    @Id
    @GeneratedValue
    var id: Long? = null
    var timestamp: Long? = null
    var status: PaymentStatus? = null
    var amount: Int? = null
    var transactionId: UUID? = null

    @ManyToOne
    @JoinColumn(name = "paymentHistory")
    val order: OrderEntity? = null
}
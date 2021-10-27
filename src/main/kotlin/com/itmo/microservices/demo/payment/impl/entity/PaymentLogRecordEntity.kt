package com.itmo.microservices.demo.payment.impl.entity

import com.itmo.microservices.demo.order.api.model.OrderStatus
import com.itmo.microservices.demo.payment.api.model.PaymentStatus
import jdk.jfr.DataAmount
import jdk.jfr.Timestamp
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class PaymentLogRecordEntity {

    @Id
    var transactionId: UUID = UUID.randomUUID()
    var timestamp: LocalDateTime = LocalDateTime.now()
    var status: PaymentStatus = PaymentStatus.FAILED
    var amount: Int = 0

    constructor()

    constructor(timestamp: LocalDateTime,
                status: PaymentStatus,
                amount: Int,
                transactionId:UUID) {
        this.timestamp = timestamp
        this.status = status
        this.amount = amount
        this.transactionId = transactionId
    }
}
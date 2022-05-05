package com.itmo.microservices.demo.order.impl.entity

import com.itmo.microservices.demo.order.common.PaymentStatus
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class PaymentLogRecordEntity {
    @Id
    var timestamp: Long? = null
    var status: PaymentStatus? = null
    var amount: Int? = null
    var transactionId: UUID? = null

    constructor()

    constructor(timestamp: Long, status: PaymentStatus, amount: Int, transactionId: UUID) {
        this.timestamp = timestamp
        this.status = status
        this.amount = amount
        this.transactionId = transactionId
    }
}

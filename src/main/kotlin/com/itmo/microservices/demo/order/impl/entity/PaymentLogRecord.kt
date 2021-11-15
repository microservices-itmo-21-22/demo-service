package com.itmo.microservices.demo.order.impl.entity

import com.itmo.microservices.demo.order.api.model.PaymentStatus
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class PaymentLogRecord {

    @Id
    @GeneratedValue
    var transactionId: UUID? = null

    var timestamp: Long? = null
    var status: PaymentStatus? = null
    var amount: Int? = null

    constructor()

    constructor(timestamp: Long?, status: PaymentStatus?, amount: Int?) {
        this.timestamp = timestamp
        this.status = status
        this.amount = amount
    }


}
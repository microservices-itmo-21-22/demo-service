package com.itmo.microservices.demo.order.impl.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class PaymentSubmission {

    @Id
    var transactionId: UUID? = null
    var timestamp: Long? = null

    constructor()

    constructor(transactionId: UUID?, timestamp: Long?) {
        this.transactionId = transactionId
        this.timestamp = timestamp
    }
}
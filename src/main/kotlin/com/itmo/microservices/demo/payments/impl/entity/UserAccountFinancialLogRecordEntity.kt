package com.itmo.microservices.demo.payments.impl.entity

import com.itmo.microservices.demo.payments.api.model.FinancialOperationType
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class UserAccountFinancialLogRecordEntity {

    @Id
    @GeneratedValue
    var paymentTransactionId: UUID? = null

    var type: FinancialOperationType? = null
    var amount: Int? = null
    var orderId: UUID? = null
    var timestamp: Long? = null

    constructor()

    constructor(
        paymentTransactionId: UUID?,
        type: FinancialOperationType?,
        amount: Int?,
        orderId: UUID?,
        timestamp: Long?
    ) {
        this.paymentTransactionId = paymentTransactionId
        this.type = type
        this.amount = amount
        this.orderId = orderId
        this.timestamp = timestamp
    }


}
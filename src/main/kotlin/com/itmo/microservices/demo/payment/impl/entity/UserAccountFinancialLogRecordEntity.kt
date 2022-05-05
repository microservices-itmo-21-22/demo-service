package com.itmo.microservices.demo.payment.impl.entity

import com.itmo.microservices.demo.payment.api.model.FinancialOperationType
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class UserAccountFinancialLogRecordEntity {
    @Id
    var id: Long? = null
    var type: FinancialOperationType? = null
    var amount: Int? = null
    var orderId: UUID? = null
    var paymentTransactionId: UUID? = null
    var timestamp: Long? = null
}
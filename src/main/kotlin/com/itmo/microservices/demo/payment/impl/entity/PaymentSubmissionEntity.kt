package com.itmo.microservices.demo.payment.impl.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class PaymentSubmissionEntity {
    @Id
    var id: Long? = null
    var timestamp: Long? = null
    var transactionId: UUID? = null
}
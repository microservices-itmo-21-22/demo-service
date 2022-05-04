package com.itmo.microservices.demo.payment.api.model

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class PaymentLogRecord {
    @Id
    var id: UUID? = null
}

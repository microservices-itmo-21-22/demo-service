package com.itmo.microservices.demo.payment.impl.entities

import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class UserAccountFinancialLogRecord {
    @Id
    @Type(type = "uuid-char")
    val id: UUID? = null
}
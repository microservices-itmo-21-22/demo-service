package com.itmo.microservices.demo.payment.impl.entity

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import java.sql.Time
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "payment304")
data class Payment (
    @Id
    @Type(type = "uuid-char")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    var Id : UUID? = null,
    var orderId : UUID? = null,
    var transactionId : UUID? = null,
    var type : Int? = null,
    var amount : Int? = null,
    var openTime : Long? = null,
    var closeTime : Long? = null
)
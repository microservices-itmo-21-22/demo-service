package com.itmo.microservices.demo.payments.impl.entity

import com.itmo.microservices.demo.payments.api.model.TransactionStatus
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Transaction {

    @Id
    @GeneratedValue
    var id: UUID? = null
    var status: TransactionStatus? = null
    var submitTime: Long? = null
    var completedTime: Long? = null
    var cost: Int? = null
    var delta: Int? = null

    constructor()

    constructor(id: UUID?, status: TransactionStatus?, submitTime: Long?, completedTime: Long?, cost: Int?, delta: Int?) {
        this.id = id
        this.status = status
        this.submitTime = submitTime
        this.completedTime = completedTime
        this.cost = cost
        this.delta = delta
    }

}
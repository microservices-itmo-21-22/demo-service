package com.itmo.microservices.demo.order.impl.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class ItemMapEntity {
    @Id
    var id : UUID? = null
    var amount : Int? = null

    constructor()

    constructor(id: UUID, amount: Int) {
        this.id = id
        this.amount = amount
    }
}

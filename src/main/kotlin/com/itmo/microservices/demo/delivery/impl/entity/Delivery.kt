package com.itmo.microservices.demo.delivery.impl.entity

import com.itmo.microservices.demo.stock.api.model.Category
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "delivery304")
class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: UUID? = null
    var address: String? = null
    var date: Date? = null

    constructor()

    constructor(id: UUID? = null, address: String? = null, date: Date?) {
        this.id = id
        this.address = address
        this.date = date
    }

}

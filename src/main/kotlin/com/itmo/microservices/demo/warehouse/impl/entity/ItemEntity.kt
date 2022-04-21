package com.itmo.microservices.demo.warehouse.impl.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class ItemEntity {
    @Id
    @GeneratedValue
    var id: UUID? = null
    var title: String? = null
    var description: String? = null
    var price: Int? = 100
    var amount: Int? = null
}

package com.itmo.microservices.demo.items.impl.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class OrderItemEntity {

    @Id
    var id: UUID = UUID.randomUUID()
    var title: String = ""
    var price: Int = 0

    constructor()

    constructor(id : UUID, title : String, price : Int) {
        this.id = id
        this.title = title
        this.price = price
    }
}
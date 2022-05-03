package com.itmo.microservices.demo.warehouse.impl.entity

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "warehouse_item")
class WarehouseItemEntity {

    @Id
    @GeneratedValue
    var id: UUID? = null
    var title: String? = null
    var description: String? = null
    var price: Int? = null
    var amount: Int? = null

    constructor()

    constructor(id: UUID?, title: String?, description: String?, price: Int?, amount: Int?) {
        this.id = id
        this.title = title
        this.description = description
        this.price = price
        this.amount = amount
    }
}
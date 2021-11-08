package com.itmo.microservices.demo.warehouse.impl.entity

import java.util.UUID
import com.fasterxml.jackson.annotation.JsonBackReference
import com.itmo.microservices.demo.warehouse.impl.entity.WarehouseItem
import javax.persistence.*

@Entity
class WarehouseItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonBackReference
    var id: UUID? = null

    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    var item: WCatalogItem? = null
    var amount: Int = 0
    var booked: Int = 0

    constructor(item: WCatalogItem?, amount: Int, booked: Int) {
        this.item = item
        this.amount = amount
        this.booked = booked
    }

    constructor() {}
}
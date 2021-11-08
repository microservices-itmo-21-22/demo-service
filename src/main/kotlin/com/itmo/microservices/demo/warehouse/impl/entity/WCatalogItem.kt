package com.itmo.microservices.demo.warehouse.impl.entity

import java.util.UUID
import com.fasterxml.jackson.annotation.JsonBackReference
import com.itmo.microservices.demo.warehouse.impl.entity.WarehouseItem
import javax.persistence.*

@Entity
class WCatalogItem {
    @Id
    @GeneratedValue
    var id: UUID? = null

    @JsonBackReference
    @OneToOne(cascade = [CascadeType.REMOVE], fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn
    var warehouseItem: WarehouseItem? = null
    var title: String? = null
    var description: String? = null
    var price: Int = 100

    constructor() {}
    constructor(
        id: UUID?,
        warehouseItem: WarehouseItem?,
        title: String?,
        description: String?,
        price: Int
    ) {
        this.id = id
        this.warehouseItem = warehouseItem
        this.title = title
        this.description = description
        this.price = price
    }
}
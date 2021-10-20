package com.itmo.microservices.demo.stock.impl.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "stock")
class StockItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: UUID? = null
    var name: String? = null
    var price: Double? = null
    var totalCount: Int? = null
    var reservedCount: Int? = null
    var categoryId: Int? = null

    constructor()

    constructor(id: UUID? = null, name: String? = null, price: Double?,
    totalCount: Int?, reservedCount: Int?, categoryId: Int?) {
        this.id = id
        this.name = name
        this.price = price
        this.totalCount = totalCount
        this.reservedCount = reservedCount
        this.categoryId = categoryId
    }

    override fun toString(): String =
        "StockItem(id=$id, name=$name, price=$price, totalCount=$totalCount, " +
                "reservedCount=$reservedCount, categoryId=$categoryId)"

    fun setReservedCount(number: Int) {
        this.reservedCount = reservedCount?.plus(number)
    }

}
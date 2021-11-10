package com.itmo.microservices.demo.stock.impl.entity

import com.itmo.microservices.demo.stock.api.model.Category
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "stock")
class StockItem {

    @Id
    @Type(type = "uuid-char")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: UUID? = null
    var name: String? = null
    var price: Double? = null
    var totalCount: Int? = null
    var reservedCount: Int? = null
    var category: Category = Category.COMMON

    constructor()

    constructor(id: UUID? = null, name: String? = null, price: Double?,
                totalCount: Int?, reservedCount: Int?, category: Category) {
        this.id = id
        this.name = name
        this.price = price
        this.totalCount = totalCount
        this.reservedCount = reservedCount
        this.category = category
    }

    override fun toString(): String =
        "StockItem(id=$id, name=$name, price=$price, totalCount=$totalCount, " +
                "reservedCount=$reservedCount, category=$category)"

    fun setReservedCount(number: Int) {
        this.reservedCount = reservedCount?.plus(number)
    }

    fun setTotalCount(number: Int) {
        this.totalCount = totalCount?.plus(number)
    }

}

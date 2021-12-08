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
    var id: UUID = UUID.randomUUID()
    var title: String? = null
    var description: String? = null
    var price: Int? = 100
    var amount: Int? = null
    var reservedCount: Int? = null
    var category: Category = Category.COMMON

    constructor()

    constructor(id: UUID, title: String? = null, description: String? = null, price: Int? = 100,
                amount: Int?, reservedCount: Int?, category: Category) {
        this.id = id
        this.title = title
        this.description = description
        this.price = price
        this.amount = amount
        this.reservedCount = reservedCount
        this.category = category
    }

    override fun toString(): String =
        "StockItem(id=$id, title=$title, description=$description, price=$price, amount=$amount, " +
                "reservedCount=$reservedCount, category=$category)"

    fun setReservedCount(number: Int) {
        this.reservedCount = reservedCount?.plus(number)
    }

    fun setAmount(number: Int) {
        this.amount = amount?.plus(number)
    }

}

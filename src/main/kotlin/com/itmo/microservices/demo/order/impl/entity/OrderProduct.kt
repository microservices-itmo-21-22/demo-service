package com.itmo.microservices.demo.order.impl.entity

import com.itmo.microservices.demo.order.api.model.ProductType
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
public class OrderProduct {
    @Id
    @GeneratedValue
    var id: UUID? = null
    var name: String? = null
    var description: String? = null
    var country: String? = null
    var price: Double? = null
    var sale: Double? = null
    var type: ProductType = ProductType.OTHER

    constructor()

    constructor(name: String? = null, description: String? = null, country: String? = null, price: Double? = null, sale: Double? = null, type: ProductType) {
        this.name = name
        this.description = description
        this.country = country
        this.price = price
        this.sale = sale
        this.type = type
    }

    override fun toString(): String {
        return "Product(id=$id, name=$name, description=$description, country=$country, price=$price, type=$type)"
    }
}
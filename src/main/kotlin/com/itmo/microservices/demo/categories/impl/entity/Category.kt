package com.itmo.microservices.demo.categories.impl.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "categories")
class Category {

    @Id
    @GeneratedValue
    var id: UUID? = null
    var name: String? = null

    constructor()

    constructor(id: UUID? = null, name: String? = null) {
        this.id = id
        this.name = name
    }

    override fun toString(): String =
        "Category(id=$id, name=$name)"

}
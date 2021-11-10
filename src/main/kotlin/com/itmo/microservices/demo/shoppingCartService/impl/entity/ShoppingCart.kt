package com.itmo.microservices.demo.shoppingCartService.impl.entity


import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Entity
@Table(name="Cart304")
class ShoppingCart {
    @Id
    @Type(type = "uuid-char")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    var id : UUID = UUID.randomUUID()
    var status: String = "ACTIVE"


    constructor()

    constructor(status: String) {
        this.status = status
    }
}
package com.itmo.microservices.demo.shoppingCartService.impl.entity


import java.util.*
import javax.persistence.*

@Entity
@Table(name="Cart304")
class ShoppingCart {
    @Id
    var id : UUID = UUID.randomUUID()
    var status: String = "ACTIVE"


    constructor()

    constructor(status: String) {
        this.status = status
    }
}
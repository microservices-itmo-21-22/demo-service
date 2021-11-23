package com.itmo.microservices.demo.shoppingCartService.impl.entity


import java.util.*
import javax.persistence.*

@Entity
@Table(name="Cart304")
class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    lateinit var id : UUID
    var status: String = "ACTIVE"


    constructor()

    constructor(status: String) {
        this.status = status
    }
}
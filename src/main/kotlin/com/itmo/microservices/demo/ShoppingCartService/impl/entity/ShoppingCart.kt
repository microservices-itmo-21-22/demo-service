package com.itmo.microservices.demo.ShoppingCartService.impl.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
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
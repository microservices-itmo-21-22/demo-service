package com.itmo.microservices.demo.payments.impl.entity

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class PaymentAppUser {

    @Id
    var name: String? = null
    var password: String? = null

    constructor()

    constructor( name: String? ,password: String?) {
        this.name = name
        this.password = password
    }

}
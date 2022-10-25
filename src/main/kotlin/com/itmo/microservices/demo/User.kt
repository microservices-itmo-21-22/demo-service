package com.itmo.microservices.demo

import java.math.BigDecimal
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.*

@Entity
class User {

    @Id
    @GeneratedValue
    var id: UUID? = null
    var name : String? = null
    var surname: String? = null
    var adress: String? = null
    var phoneNumber: BigDecimal? = null

    constructor()


    constructor(id: UUID?, name: String?, surname: String?, adress: String?,phoneNumber: BigDecimal?) {
        this.id = id
        this.name = name
        this.surname = surname
        this.adress = adress
        this.phoneNuimber = phoneNumber
    }
}

package com.itmo.microservices.demo.users.impl.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class AppUser {

    @Id
    var id: UUID? = null
    var ipaddress: String? = null
    var name: String? = null
    var username: String? = null
    var email: String? = null
    var password: String? = null
    var phone: String? = null
    var lastBasketId: UUID? = null

    constructor()

    constructor(id: UUID?, ipaddress: String?, name: String?,  username: String?, email: String?, password: String?, phone: String?, lastBasketId: UUID?) {
        this.id = id
        this.ipaddress = ipaddress
        this.username = username
        this.name = name
        this.email = email
        this.password = password
        this.phone = phone
        this.lastBasketId = lastBasketId
    }

    constructor(id: UUID?) {
        this.id = id
    }




}
package com.itmo.microservices.demo.users.impl.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class AppUser {
    @Id
    var id:UUID? = UUID.randomUUID()
    var name: String? = null
    var password: String? = null

    constructor()

    constructor(name: String?, password: String?) {
        this.name = name
        this.password = password
    }

}

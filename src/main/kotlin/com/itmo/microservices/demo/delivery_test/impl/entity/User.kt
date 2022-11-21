package com.itmo.microservices.demo.users.impl.entity

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class User {

    @Id
    var id: UUID? = null
    var name: String? = null
    var password: String? = null

    constructor()

    constructor(id: UUID?, name: String?, password: String?) {
        this.id = id
        this.name = name
        this.password = password
    }
}
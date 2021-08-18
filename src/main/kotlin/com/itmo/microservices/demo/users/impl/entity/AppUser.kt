package com.itmo.microservices.demo.users.impl.entity

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class AppUser {

    @Id
    var username: String? = null
    var name: String? = null
    var surname: String? = null
    var email: String? = null
    var password: String? = null

    constructor()

    constructor(username: String?, name: String?, surname: String?, email: String?, password: String?) {
        this.username = username
        this.name = name
        this.surname = surname
        this.email = email
        this.password = password
    }
}
package com.itmo.microservices.demo.users.impl.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class AppUser {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false)
    var id: UUID? = null
    @Column(unique = true)
    var username: String? = null
    @JsonIgnore
    var password: String? = null

    constructor()

    constructor(username: String?, password: String?) {
        this.username = username
        this.password = password
    }
}
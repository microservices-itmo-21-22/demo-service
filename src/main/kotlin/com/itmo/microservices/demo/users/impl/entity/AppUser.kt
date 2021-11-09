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
    @Type(type = "uuid-char")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    var id: UUID? = UUID.randomUUID()
    @Column(unique = true)
    var name: String? = null
    @JsonIgnore
    var password: String? = null

    constructor()

    constructor(name: String?, password: String?) {
        this.name = name
        this.password = password
    }
}
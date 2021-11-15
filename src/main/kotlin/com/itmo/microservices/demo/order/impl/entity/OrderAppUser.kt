package com.itmo.microservices.demo.order.impl.entity

import javax.persistence.*

@Entity
class OrderAppUser {

    @Id
    var username: String? = null
    var name: String? = null
    var surname: String? = null
    var email: String? = null
    var password: String? = null

    @JoinTable(
        name = "buskets",
        joinColumns = [JoinColumn(
            name = "user_order_id",
        )],
        inverseJoinColumns = [JoinColumn(
            name = "busket_id",
        )]
    )
    @OneToMany
    var buskets: List<Busket>? = null

    constructor()

    constructor(username: String?, name: String?, surname: String?, email: String?, password: String?, buskets: List<Busket>?) {
        this.username = username
        this.name = name
        this.surname = surname
        this.email = email
        this.password = password
        this.buskets = buskets
    }

}
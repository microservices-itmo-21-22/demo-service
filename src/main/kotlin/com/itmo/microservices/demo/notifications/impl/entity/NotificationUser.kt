package com.itmo.microservices.demo.notifications.impl.entity

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class NotificationUser {
    @Id
    var name: String? = null

    constructor()

    constructor(name: String) {
        this.name = name
    }
}
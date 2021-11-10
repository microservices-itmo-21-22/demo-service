package com.itmo.microservices.demo.notifications.impl.entity

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class NotificationUser {
    @Id
    var name: String? = null
    // Ignoring surname because we don't need it for notifications

    constructor()

    constructor(name: String) {
        this.name = name
    }
}
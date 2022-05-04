package com.itmo.microservices.demo.delivery.impl.entity

import java.util.*
import javax.persistence.*

@Entity
class BookingEntity {
    @Id
    @GeneratedValue
    var id: UUID? = null
    @ElementCollection
    var failedItems: Set<UUID> = emptySet()

    constructor()

    constructor(id: UUID, failedItems: Set<UUID>) {
        this.id = id
        this.failedItems = failedItems
    }
}
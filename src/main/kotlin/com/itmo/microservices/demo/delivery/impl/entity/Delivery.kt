package com.itmo.microservices.demo.delivery.impl.entity

import com.itmo.microservices.demo.delivery.api.model.DeliveryType
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Delivery {
    @Id
    var id: UUID? = null
    var user: String? = null
    var type: DeliveryType? = null
    var warehouse: Int? = null
    var preferredDeliveryTime: LocalDateTime? = null
    var address: String? = null
    var courierCompany: String? = null

    constructor()

    constructor(id: UUID?,
                user: String?,
                type: DeliveryType?,
                warehouse: Int?,
                preferredDeliveryTime: LocalDateTime?,
                address: String?,
                courierCompany: String?
    ) {
        this.id = id
        this.user = user
        this.type = type
        this.warehouse = warehouse
        this.preferredDeliveryTime = preferredDeliveryTime
        this.address = address
        this.courierCompany = courierCompany
    }

    override fun toString(): String =
        "Delivery(id=$id, " +
                "user=$user, " +
                "type=$type, " +
                "warehouse=$warehouse, " +
                "preferredDeliveryTime=$preferredDeliveryTime, " +
                "address=$address, " +
                "courierCompany=$courierCompany" +
                ")"

}
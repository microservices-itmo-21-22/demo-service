package com.itmo.microservices.demo.delivery.impl.entity

import com.itmo.microservices.demo.delivery.api.model.DeliveryType
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
    var deliveryDuration: Int? = null
    var address: String? = null
    var courierCompany: String? = null

    constructor()

    constructor(id: UUID?, user: String?, type: DeliveryType?, warehouse: Int?, deliveryDuration: Int?, address: String?, courierCompany: String?) {
        this.id = id
        this.user = user
        this.type = type
        this.warehouse = warehouse
        this.deliveryDuration = deliveryDuration
        this.address = address
        this.courierCompany = courierCompany
    }

    override fun toString(): String =
        "Task(id=$id, user=$user, type=$type, warehouse=$warehouse, deliveryDuration=$deliveryDuration, address=$address, courierCompany=$courierCompany)"

}
package com.itmo.microservices.demo.order.api.model

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class ItemMap {
    @Id
    var id : UUID? = null
    var item : Int? = null
}

package com.itmo.microservices.demo.orders.impl.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.itmo.microservices.demo.orders.api.model.OrderStatus
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.springframework.web.bind.annotation.Mapping
import java.util.*
import javax.persistence.*
import kotlin.collections.HashMap

@Entity
@Table(name = "order304")
@JsonInclude(JsonInclude.Include.NON_NULL)
class Order {
    @Id
    @Type(type = "uuid-char")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    var id : UUID = UUID.randomUUID()
    var timeCreated : Long = 0
    var status : OrderStatus = OrderStatus.COLLECTING
    var basketId : UUID? = null
    var userId : UUID = UUID.randomUUID()
    @ElementCollection
    var itemsMap : MutableMap<UUID, Long> = mutableMapOf()
    var deliveryDuration : Int? = null
    @OneToMany
    var paymentHistory: List<PaymentLogRecord> = listOf()


    constructor()

    constructor(id : UUID, timeCreated : Long, status : OrderStatus, userId : UUID) {
        this.id = id
        this.status = status
        this.userId = userId
        this.timeCreated = timeCreated
    }

}
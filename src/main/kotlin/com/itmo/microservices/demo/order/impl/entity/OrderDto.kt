package com.itmo.microservices.demo.order.impl.entity
//
//import java.util.*
//import javax.persistence.ElementCollection
//import javax.persistence.Entity
//import javax.persistence.Id
//
//@Entity
//class OrderDto {
//    @Id
//    var timeCreated: Long = 0
//    var status: OrderStatus? = null
//    @ElementCollection
//    var itemsMap: Map<UUID, Int>? = null
//    var deliveryDuration: Int? = null
//    @ElementCollection
//    var paymentHistory: List<PaymentLogRecord>? = null
//
//    constructor() {}
//
//    constructor(timeCreated: Long, status: OrderStatus, deliveryDuration : Int?) {
//        this.timeCreated = timeCreated
//        this.status = status
//        this.itemsMap = emptyMap()
//        this.deliveryDuration = deliveryDuration
//        this.paymentHistory = emptyList()
//    }
//}
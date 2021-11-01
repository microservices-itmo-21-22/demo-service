package com.itmo.microservices.demo.order.api.dto

import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Data
import java.time.LocalDateTime
import java.util.*

@Data
@Builder
@AllArgsConstructor
class Order {
    private val uuid: UUID = UUID.randomUUID()
    private val timeCreated: LocalDateTime = LocalDateTime.now()
    private val itemList: Map<OrderItem, Int>
    private val status: OrderStatus

    init {
        itemList = HashMap()
        status = OrderStatus.COLLECTING
    }
}
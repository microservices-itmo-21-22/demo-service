package com.itmo.microservices.demo.order.impl.util

import com.itmo.microservices.demo.items.impl.util.toModel
import com.itmo.microservices.demo.order.api.model.OrderDto
import com.itmo.microservices.demo.order.api.model.OrderItemDto
import com.itmo.microservices.demo.order.impl.entities.OrderEntity
import com.itmo.microservices.demo.order.impl.entities.OrderItem
import com.itmo.microservices.demo.order.impl.repository.OrderItemRepository

fun OrderEntity.toModel(orderItemRepository: OrderItemRepository): OrderDto = OrderDto(
    id = this.id,
    userId = this.userId,
    timeCreated = this.timeCreated,
    status = this.status,
    deliveryDuration = this.deliveryDuration,
    itemsMap = this.listToMap(orderItemRepository),
    paymentHistory = null //TODO: bind with payment service
)

fun OrderEntity.listToMap(orderItemRepository: OrderItemRepository): Map<OrderItemDto, Int>? {
    //get list of all items bounded with current order
    val orderItemList: List<OrderItem>? = orderItemRepository.findByOrderEntity(this)
    //create empty map for future dto
    val orderItemMap: Map<OrderItemDto, Int> = mutableMapOf()
    if (orderItemList == null) return null
    //refactor list to map
    for (orderItem in orderItemList) {
        orderItemMap.plus(Pair(orderItem.toModel(), orderItem.amount))
    }
    return orderItemMap
}

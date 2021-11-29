package com.itmo.microservices.demo.order.api.messaging

import com.itmo.microservices.demo.order.api.model.OrderDto

data class OrderCreatedEvent(val order: OrderDto)
data class OrderGetEvent(val order: OrderDto)
data class ItemAddedToOrder(val order: OrderDto)
data class OrderRegistered(val order: OrderDto)
data class OrderDated(val order: OrderDto)
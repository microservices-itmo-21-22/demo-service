package com.itmo.microservices.demo.order.api.messaging

import com.itmo.microservices.demo.order.api.model.OrderDto

data class OrderCreatedEvent(val order: OrderDto)

data class OrderGetEvent(val order: OrderDto)

data class OrderGetAllEvent(val order: OrderDto?)

data class OrderDeletedEvent(val order: OrderDto)
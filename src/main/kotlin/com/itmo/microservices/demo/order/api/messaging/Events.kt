package com.itmo.microservices.demo.order.api.messaging

import com.itmo.microservices.demo.order.api.model.OrderModel

data class OrderCreatedEvent(val order: OrderModel)

data class OrderGetEvent(val order: OrderModel)

data class OrderGetAllEvent(val order: OrderModel?)

data class OrderDeletedEvent(val order: OrderModel)
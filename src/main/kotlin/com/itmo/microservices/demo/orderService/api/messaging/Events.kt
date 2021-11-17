package com.itmo.microservices.demo.orderService.api.messaging

import com.itmo.microservices.demo.orders.api.model.*

data class OrderCreatedEvent(val task: OrderModel)

data class OrderDeletedEvent(val task: OrderModel)

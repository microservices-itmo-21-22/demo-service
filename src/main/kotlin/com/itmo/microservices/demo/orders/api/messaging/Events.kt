package com.itmo.microservices.demo.orders.api.messaging

import com.itmo.microservices.demo.orders.api.model.OrderModel

data class OrderCreatedEvent(val task: OrderModel)

data class OrderDeletedEvent(val task: OrderModel)
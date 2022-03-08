package com.itmo.microservices.demo.delivery.impl.event

import com.itmo.microservices.demo.order.api.model.OrderStatus
import java.util.*

data class OrderStatusChanged(val orderId: UUID, val newStatus: OrderStatus)
package com.itmo.microservices.demo.order.impl.util

import com.itmo.microservices.demo.order.api.model.Order
import com.itmo.microservices.demo.warehouse.impl.entity.OrderEntity

fun OrderEntity.toModel(): Order = Order(id, timeCreated, status, itemsMap, deliveryDuration, paymentHistory)
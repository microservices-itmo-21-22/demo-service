package com.itmo.microservices.demo.orders.impl.util

import com.itmo.microservices.demo.orders.api.model.BookingDto
import com.itmo.microservices.demo.orders.api.model.OrderDto
import com.itmo.microservices.demo.orders.api.model.OrderModel
import com.itmo.microservices.demo.orders.impl.entity.Order
import com.itmo.microservices.demo.stock.impl.entity.StockItem
import java.util.*

//fun OrderModel.toEntity() = Order(
//    id = this.id,
//    basketId = this.basketId,
//    date = this.date,
//    userId = this.userId,
//    status = this.status
//)
//
//fun Order.toModel(): OrderModel = OrderModel(
//    id = this.id,
//    basketId = this.basketId,
//    date = this.date,
//    userId = this.userId,
//    status = this.status
//)
fun Order.toDto() : OrderDto = OrderDto(
    id = this.id,
    timeCreated = this.timeCreated,
    status = this.status,
    itemsMap = this.itemsMap,
    deliveryDuration = this.deliveryDuration,
    paymentHistory = this.paymentHistory
)
fun Order.toBookingDto(failed : Set<UUID>) : BookingDto = BookingDto(
    id = this.id,
    failedItems = failed
)
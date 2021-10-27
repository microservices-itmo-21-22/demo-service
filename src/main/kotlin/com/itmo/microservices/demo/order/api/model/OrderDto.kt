package com.itmo.microservices.demo.order.api.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.itmo.microservices.demo.items.api.model.OrderItem
import com.itmo.microservices.demo.payment.api.model.PaymentLogRecordDto
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OrderDto(
    val id: UUID?,
    val userId: UUID?,
    val timeCreated: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
    val status: OrderStatus = OrderStatus.COLLECTING,
    val itemsMap: Map<OrderItem, Int>?,
    val deliveryDuration: Int?,
    val paymentHistory: List<PaymentLogRecordDto>?)
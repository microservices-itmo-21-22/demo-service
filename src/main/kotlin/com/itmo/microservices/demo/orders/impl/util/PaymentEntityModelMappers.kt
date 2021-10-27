package com.itmo.microservices.demo.orders.impl.util

import com.itmo.microservices.demo.orders.api.model.PaymentModel
import com.itmo.microservices.demo.orders.impl.entity.Payment

fun PaymentModel.toEntity() : Payment = Payment(
    orderId = this.orderId,
    type = this.type,
    amount = this.amount,
    time = this.time
)

fun Payment.toModel(): PaymentModel = PaymentModel(
    orderId = this.orderId,
    type = this.type,
    amount = this.amount,
    time = this.time
)
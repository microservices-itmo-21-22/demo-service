package com.itmo.microservices.demo.orders.impl.util

import com.itmo.microservices.demo.orders.api.model.PaymentModel
import com.itmo.microservices.demo.orders.impl.entity.OldPayment

fun PaymentModel.toEntity() : OldPayment = OldPayment(
    orderId = this.orderId,
    type = this.type,
    amount = this.amount,
    time = this.time
)

fun OldPayment.toModel(): PaymentModel = PaymentModel(
    orderId = this.orderId,
    type = this.type,
    amount = this.amount,
    time = this.time
)
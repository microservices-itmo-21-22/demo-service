package com.itmo.microservices.demo.payments.impl.util

import com.itmo.microservices.demo.payments.api.model.PaymentModel
import com.itmo.microservices.demo.payments.impl.entity.Payment

fun PaymentModel.toEntity(): Payment = Payment(
    date = this.date,
    status = this.status,
    username = this.username
)

fun Payment.toModel(): PaymentModel = PaymentModel(
    id = this.id,
    date = this.date,
    status = this.status,
    username = this.username
)
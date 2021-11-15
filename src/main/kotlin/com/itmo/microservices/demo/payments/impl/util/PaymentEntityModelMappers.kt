package com.itmo.microservices.demo.payments.impl.util

import com.itmo.microservices.demo.payments.api.model.PaymentModel
import com.itmo.microservices.demo.payments.impl.entity.Payment
import com.itmo.microservices.demo.payments.impl.entity.PaymentAppUser
import com.itmo.microservices.demo.users.impl.entity.AppUser

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

fun AppUser.toPaymentAppUser(): PaymentAppUser = PaymentAppUser(
    name = this.name,
    password = this.password
)
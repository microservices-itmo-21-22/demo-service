package com.itmo.microservices.demo.payments.impl.util

import com.itmo.microservices.demo.payments.api.model.PaymentModel
import com.itmo.microservices.demo.payments.impl.entity.Payment
import com.itmo.microservices.demo.payments.impl.entity.PaymentAppUser
import com.itmo.microservices.demo.users.impl.entity.AppUser

fun PaymentModel.toEntity(): Payment = Payment(
    date = this.date,
    status = this.status,
    user = this.user
)

fun Payment.toModel(): PaymentModel = PaymentModel(
    id = this.id,
    date = this.date,
    status = this.status,
    user = this.user
)

fun AppUser.toPaymentAppUser(): PaymentAppUser = PaymentAppUser(
    username = this.username,
    name = this.name,
    surname = this.surname,
    email = this.email,
    password = this.password
)
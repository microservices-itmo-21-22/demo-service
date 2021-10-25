package com.itmo.microservices.demo.payments.api.model

import com.itmo.microservices.demo.payments.impl.entity.PaymentAppUser
import java.util.*

data class PaymentModel (
    val id: UUID?,
    val date: Date?,
    val status: Int?,
    val user: PaymentAppUser?
)
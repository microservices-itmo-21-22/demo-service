package com.itmo.microservices.demo.payments.api.messaging

import com.itmo.microservices.demo.payments.api.model.PaymentModel

data class PaymentProccessedEvent(val payment: PaymentModel)
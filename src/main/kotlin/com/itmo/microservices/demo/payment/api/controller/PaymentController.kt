package com.itmo.microservices.demo.payment.api.controller

import com.itmo.microservices.demo.payment.api.model.PaymentRequestDto
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/payment")
class PaymentController {
    fun executePayment(accessToken: String, dto: PaymentRequestDto) {

    }
}
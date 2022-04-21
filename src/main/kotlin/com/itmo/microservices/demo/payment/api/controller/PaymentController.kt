package com.itmo.microservices.demo.payment.api.controller

import com.itmo.microservices.demo.payment.api.model.PaymentLogRecord
import com.itmo.microservices.demo.payment.api.service.PaymentService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
//@RequestMapping("/payment")
class PaymentController(
    val service: PaymentService
) {
    @GetMapping("/finlog")
    fun getFinancialLog(@RequestParam("order_id") orderId: UUID?):
            List<PaymentLogRecord> = service.getFinancialLog(orderId)
}
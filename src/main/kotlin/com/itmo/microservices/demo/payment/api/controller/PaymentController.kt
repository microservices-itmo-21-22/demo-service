package com.itmo.microservices.demo.payment.api.controller

import com.itmo.microservices.demo.payment.api.model.PaymentSubmissionDto
import com.itmo.microservices.demo.payment.api.model.UserAccountFinancialLogRecordDto
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.*

@Controller
class PaymentController {


    @PostMapping("/orders/{order_id}/payment")
    fun getPaymentSubmission(order_id: Int): PaymentSubmissionDto {
        return PaymentSubmissionDto(0, UUID.randomUUID())
    }

    @GetMapping("/finlog")
    fun getUserAccountFinInfoLog(@RequestParam order_id: UUID): List<UserAccountFinancialLogRecordDto> {
        return emptyList()
    }

}
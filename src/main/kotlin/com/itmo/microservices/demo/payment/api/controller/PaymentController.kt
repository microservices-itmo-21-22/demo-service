package com.itmo.microservices.demo.payment.api.controller

import com.itmo.microservices.demo.payment.api.model.PaymentSubmissionDto
import com.itmo.microservices.demo.payment.api.model.UserAccountFinancialLogRecordDto
import com.itmo.microservices.demo.payment.impl.service.PaymentServiceImpl
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.*

@Controller
class PaymentController(private val paymentServiceImpl: PaymentServiceImpl) {


    //Оплата заказа
    @PostMapping("/orders/{order_id}/payment")
    fun postPaymentSubmission(orderId: UUID): PaymentSubmissionDto {
        return paymentServiceImpl.orderPayment(orderId)
    }
    //Получение информации о финансовых операциях с аккаунтом пользователя
    @GetMapping("/finlog")
    fun getUserAccountFinInfoLog(@RequestParam orderId: UUID): List<UserAccountFinancialLogRecordDto> {
        return paymentServiceImpl.getPaymentFinLog(orderId)
    }

}
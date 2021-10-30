package com.itmo.microservices.demo.payment.api.controller

import com.itmo.microservices.demo.payment.api.model.PaymentModel
import com.itmo.microservices.demo.payment.api.model.PaymentRequestDto
import com.itmo.microservices.demo.payment.api.model.PaymentSubmissionDto
import com.itmo.microservices.demo.payment.api.service.PaymentServiceImpl
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/payment")
class PaymentController(val paymentService: PaymentServiceImpl) {

    @PostMapping("/transaction")
    @Operation(
        summary = "Execute Payment",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Payment Failed", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun executePayment(@Parameter(hidden = false) @AuthenticationPrincipal @RequestBody dto: PaymentRequestDto): PaymentSubmissionDto =
        paymentService.executePayment(dto.toModel())

    fun PaymentRequestDto.toModel(): PaymentModel = PaymentModel(this.orderId)
}
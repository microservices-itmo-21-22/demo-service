package com.itmo.microservices.demo.payment.api.controller

import com.itmo.microservices.demo.payment.api.model.PaymentSubmissionDto
import com.itmo.microservices.demo.payment.api.model.UserAccountFinancialLogRecordDto
import com.itmo.microservices.demo.payment.api.service.PaymentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/")
class PaymentController(private val paymentService: PaymentService) {

    @GetMapping("/finlog")
    @Operation(
            summary = "Get user financial log",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
                ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getFinLog(
            @RequestParam(value = "order_id") orderId: UUID
    ): List<UserAccountFinancialLogRecordDto> {
        return paymentService.getFinLog(orderId)
    }

    @PostMapping("/orders/{order_id}/payment")
    @Operation(
            summary = "Make payment for order",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()]),
                ApiResponse(description = "Service unavailable", responseCode = "503")
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun makePayment(@PathVariable order_id : UUID) : PaymentSubmissionDto = paymentService.makePayment(order_id)

}
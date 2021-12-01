package com.itmo.microservices.demo.payment.api.controller

import com.itmo.microservices.demo.payment.api.model.PaymentSubmissionDto
import com.itmo.microservices.demo.payment.api.model.UserAccountFinancialLogRecordDto
import com.itmo.microservices.demo.payment.api.service.PaymentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/")
class PaymentController(private val paymentService: PaymentService) {

    @GetMapping("/finlog?orderId={order_id}")
    @Operation(
            summary = "Get user financial log",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getFinancialLog(@PathVariable order_id : UUID) : List<UserAccountFinancialLogRecordDto> = paymentService.getFinLog(order_id)

    @GetMapping("/orders/{order_id}/payment")
    @Operation(
            summary = "Get user financial log",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun makePayment(@PathVariable order_id : UUID) : PaymentSubmissionDto = paymentService.makePayment(order_id)

}
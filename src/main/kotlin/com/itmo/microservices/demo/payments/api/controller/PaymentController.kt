package com.itmo.microservices.demo.payments.api.controller

import com.itmo.microservices.demo.payments.api.model.PaymentModel
import com.itmo.microservices.demo.payments.api.service.PaymentService
import com.itmo.microservices.demo.payments.impl.entity.Payment
import com.itmo.microservices.demo.payments.impl.entity.PaymentAppUser
import com.itmo.microservices.demo.tasks.api.model.TaskModel
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/payment")
class PaymentController(private val paymentService: PaymentService) {

    @GetMapping
    @Operation(
        summary = "Get all transactions of user by username",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getUserTransactionsInfo(@Parameter(hidden = true) @AuthenticationPrincipal userDetails: UserDetails): List<PaymentModel> =
        paymentService.getUserTransactionsInfo(userDetails)

    @PostMapping("/{paymentId}")
    @Operation(
        summary = "Refund money",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(
                description = "Unauthorized",
                responseCode = "403",
                content = [Content()]
            ),
            ApiResponse(description = "Payment not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun refund(
        @PathVariable paymentId: UUID,
        @Parameter(hidden = true) @AuthenticationPrincipal userDetails: UserDetails
    ) =
        paymentService.refund(paymentId, userDetails)

    @PostMapping
    @Operation(
        summary = "Pay",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun pay(
        @Parameter(hidden = true) @AuthenticationPrincipal userDetails: UserDetails
    ) =
        paymentService.pay(userDetails)
}
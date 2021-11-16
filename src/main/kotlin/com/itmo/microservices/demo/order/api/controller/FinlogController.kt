package com.itmo.microservices.demo.order.api.controller

import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.payment.api.model.UserAccountFinancialLogRecordDto
import com.itmo.microservices.demo.payment.api.service.PaymentService
import com.itmo.microservices.demo.users.api.exception.UserNotFoundException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/finlog")
class FinlogController(private val paymentService: PaymentService) {
    @GetMapping
    @Operation(
        summary = "Obtain information on financial transactions with user account",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getFinlog(
        @Parameter(hidden = true)
        @AuthenticationPrincipal user: UserDetails,
        @RequestParam(value = "order_id", required = false) orderId: UUID
    ): List<UserAccountFinancialLogRecordDto> {
        return try {
            paymentService.getFinlog(user.username, orderId)
        } catch (e: UserNotFoundException) {
            throw NotFoundException(e)
        }
    }
}

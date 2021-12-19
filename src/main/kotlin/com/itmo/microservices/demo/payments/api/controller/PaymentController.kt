package com.itmo.microservices.demo.payments.api.controller


import com.itmo.microservices.demo.payments.api.service.PaymentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.net.URI
import org.springframework.web.reactive.function.client.WebClient;
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*

@RestController
@RequestMapping("/payment")
class PaymentController(private val paymentService: PaymentService) {

    @PostMapping("/{orderId}/payment")
    @Operation(
            summary = "Оплата заказа",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()]),
                ApiResponse(description = "Not found", responseCode = "404", content = [Content()])
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun pay(@PathVariable orderId: UUID, @Parameter(hidden = true) @AuthenticationPrincipal author: UserDetails) =
            paymentService.pay(orderId)

    @GetMapping("/finlog?orderId={orderId}")
    @Operation(
            summary = "Получение информации о финансовых операциях с аккаунтом пользователя",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()]),
                ApiResponse(description = "Not found", responseCode = "404", content = [Content()])
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getFinlogByOrderId(@PathVariable orderId: UUID, @Parameter(hidden = true) @AuthenticationPrincipal author: UserDetails) =
            paymentService.finlog(orderId, author)

    @GetMapping("/finlog")
    @Operation(
            summary = "Получение информации о финансовых операциях с аккаунтом пользователя",
            responses = [
                ApiResponse(description = "OK", responseCode = "200"),
                ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()]),
                ApiResponse(description = "Not found", responseCode = "404", content = [Content()])
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getFinlog(@Parameter(hidden = true) @AuthenticationPrincipal author: UserDetails) =
            paymentService.finlog(null, author)

    @GetMapping("/transactions")
    @Operation(
        summary = "Получение всех транзакций",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()]),
            ApiResponse(description = "Not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getTransactions(@Parameter(hidden = true) @AuthenticationPrincipal author: UserDetails) =
        paymentService.getTransactions()

}
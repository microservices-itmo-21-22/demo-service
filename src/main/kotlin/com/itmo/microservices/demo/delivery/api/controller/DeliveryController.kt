package com.itmo.microservices.demo.delivery.api.controller


import com.itmo.microservices.demo.delivery.api.service.DeliveryService
import com.itmo.microservices.demo.order.api.model.OrderDto
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
class DeliveryController (private val deliveryService: DeliveryService){
    @GetMapping("/delivery/slots")
    @Operation(
        summary = "Get slots",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getSlots(@AuthenticationPrincipal @RequestParam number:Int)=deliveryService.getSlots(number)


    @GetMapping("/_internal/deliveryLog/{orderId}")
    @Operation(
        summary = "Получить историю доставки заказа по orderId",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getDeliveryHistoryById(@PathVariable orderId:String)=deliveryService.getDeliveryHistoryById(orderId)

    @PostMapping("/_internal/delivery")
    @Operation(
        summary = "Send order info to delivery system",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun delivery(@RequestBody request:OrderDto)=deliveryService.delivery(request)


    @PostMapping("/_internal/delivery/{times}")
    @Operation(
        summary = "Send order info to delivery system",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun delivery(@RequestBody request:OrderDto, @PathVariable times:Int)=deliveryService.delivery(request,times)


}
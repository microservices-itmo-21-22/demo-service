package com.itmo.microservices.demo.order.api.controller

import com.itmo.microservices.demo.delivery.api.model.BookingDto
import com.itmo.microservices.demo.order.api.model.OrderModel
import com.itmo.microservices.demo.order.api.service.OrderService
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/orders")
class OrderController(
    val orderService: OrderService
) {
    @PostMapping
    fun createOrder(): OrderModel = orderService.createOrder()

    @GetMapping("/{order_id}")
    fun getOrder(@PathVariable order_id: UUID): OrderModel = orderService.getOrder(order_id)

    @PutMapping("/{order_id}/items/{item_id}")
    fun moveItemToCart(@PathVariable order_id: UUID, @PathVariable item_id: UUID, @RequestParam amount: Int) = orderService.moveItemToCart(order_id, item_id, amount)

    @PostMapping("/{order_id}/bookings")
    fun finalizeOrder(@PathVariable order_id: UUID): BookingDto = orderService.finalizeOrder(order_id)

    @PostMapping("/{order_id}/delivery")
    fun setDeliverySlot(@PathVariable order_id: UUID, @RequestParam slot_in_sec: Int): BookingDto = orderService.setDeliverySlot(order_id, slot_in_sec)
}

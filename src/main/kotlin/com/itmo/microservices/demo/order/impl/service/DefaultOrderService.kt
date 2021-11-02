package com.itmo.microservices.demo.order.impl.service

import com.itmo.microservices.demo.order.api.model.OrderDto
import com.itmo.microservices.demo.order.api.service.OrderService
import com.itmo.microservices.demo.order.impl.repository.OrderRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.webjars.NotFoundException
import toModel
import java.util.*

@Service
class DefaultOrderService(private val orderRepository: OrderRepository) : OrderService {
    override fun getOrder(order_id: UUID): OrderDto {
        var optionalOrder = orderRepository.findById(order_id);
        if (optionalOrder.isEmpty) {
            throw NotFoundException("Order with Order ID $order_id not found");
        }
        return orderRepository.findById(order_id).get().toModel();
    }
}
package com.itmo.microservices.demo.warehouse.impl.service

import com.itmo.microservices.demo.order.api.model.Order
import com.itmo.microservices.demo.order.api.model.OrderStatus
import com.itmo.microservices.demo.order.api.service.OrderService
import com.itmo.microservices.demo.order.impl.util.toModel
import com.itmo.microservices.demo.warehouse.impl.entity.OrderEntity
import com.itmo.microservices.demo.warehouse.impl.repository.OrderRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class DefaultOrderService(
    private val orderRepository: OrderRepository
) : OrderService {

    override fun createOrder(): Order {
        val order = OrderEntity()
        order.timeCreated = Date().time
        order.status = OrderStatus.COLLECTING
        return orderRepository.save(order).toModel()
    }
}

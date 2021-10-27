package com.itmo.microservices.demo.order.impl.service

import com.itmo.microservices.demo.order.api.model.OrderDto
import com.itmo.microservices.demo.order.api.service.OrderService
import com.itmo.microservices.demo.order.impl.entity.OrderEntity
import com.itmo.microservices.demo.tasks.impl.repository.OrderRepository
import com.itmo.microservices.demo.users.impl.repository.UserRepository
import com.itmo.microservices.demo.users.impl.service.DefaultUserService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class DefaultOrderService(private val orderRepository: OrderRepository) : OrderService {
    override fun getOrder(order_id: UUID): OrderDto {
        TODO("Not yet implemented")
    }

    override fun createOrder(user: UserDetails): OrderDto {
        TODO("Not yet implemented")
    }
}
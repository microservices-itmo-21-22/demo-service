package com.itmo.microservices.demo.order.impl.service

import com.itmo.microservices.demo.order.api.model.OrderDto
import com.itmo.microservices.demo.order.api.service.OrderService
import org.springframework.stereotype.Service
import java.util.*

@Service
class DefaultOrderService : OrderService {
    override fun getOrder(order_id: UUID): OrderDto {
        TODO("Not yet implemented")
    }
}
package com.itmo.microservices.demo.order.impl.service

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.itmo.microservices.demo.delivery.impl.event.OrderStatusChanged
import com.itmo.microservices.demo.order.api.service.OrderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class OrderEventListener {

    @Autowired
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var eventBus: EventBus

    @PostConstruct
    fun init(){
        eventBus.register(this)
    }

    @Subscribe
    fun onOrderStatusChanged(event: OrderStatusChanged) {
        orderService.changeOrderStatus(event.orderId, event.newStatus)
    }
}
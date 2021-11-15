package com.itmo.microservices.demo.order

import com.google.common.eventbus.EventBus
import com.itmo.microservices.demo.order.api.model.OrderDto
import com.itmo.microservices.demo.order.impl.entity.OrderEntity
import com.itmo.microservices.demo.order.impl.repository.OrderRepository
import com.itmo.microservices.demo.order.impl.service.OrderServiceImpl
import com.itmo.microservices.demo.order.impl.util.toModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

class OrderTest {

//    private val orderRepository = Mockito.mock(OrderRepository::class.java)
//    private val orderId = UUID.randomUUID()
//
//    private fun orderMock(): OrderEntity {
//        return OrderEntity(date = Date(2021, 3, 4)).also { it.id = orderId }
//    }
//
//    @Test
//    fun allOrdersTest() {
//        val orderService = OrderServiceImpl(orderRepository, EventBus())
//        Mockito.`when`(orderRepository.findAll()).thenReturn(mutableListOf(orderMock()))
//        val actual = orderService.allOrders()
//        val expected = listOf(orderMock().toModel())
//        Assertions.assertEquals(actual, expected)
//    }
//
//    @Test
//    fun getOrderByIdTest() {
//        val orderService = OrderServiceImpl(orderRepository, EventBus())
//        Mockito.`when`(orderRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(orderMock()))
//        val actual = orderService.getOrderById(orderId)
//        val expected = orderMock().toModel()
//        Assertions.assertEquals(actual, expected)
//    }
//
//    @Test
//    fun createOrderTest() {
//        val orderRepository = Mockito.mock(OrderRepository::class.java)
//        val orderService = OrderServiceImpl(orderRepository, EventBus())
//
//        val actual = OrderDto(id = null, date = Date(), busket = null)
//        val orderEntity = OrderEntity(actual.date)
//
//        Mockito.`when`(orderRepository.save(Mockito.any())).thenReturn(orderEntity)
//
//        val user = Mockito.mock(UserDetails::class.java)
//        val expected = orderService.createOrder(actual, user)
//
//        Assertions.assertEquals(actual, expected)
//    }

}
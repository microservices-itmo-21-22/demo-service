//package com.itmo.microservices.demo.orders.impl.service
//
//import com.google.common.eventbus.EventBus
//import com.itmo.microservices.demo.orders.api.service.OrderService
//import com.itmo.microservices.demo.orders.impl.entity.Order
//import com.itmo.microservices.demo.orders.impl.repository.OrderRepository
//import com.itmo.microservices.demo.orders.impl.repository.PaymentRepository
//import com.itmo.microservices.demo.orders.impl.util.toModel
//import com.itmo.microservices.demo.stock.impl.util.toModel
//import com.itmo.microservices.demo.users.api.service.UserService
//import com.itmo.microservices.demo.users.impl.service.DefaultUserService
//import junit.framework.Assert
//import org.junit.Test
//import org.mockito.Mockito
//import org.springframework.data.repository.findByIdOrNull
//import org.springframework.security.core.GrantedAuthority
//import org.springframework.security.core.userdetails.UserDetails
//import java.util.*
//
//internal class DefaultOrderServiceTests {
//
//    private val orderRepository = Mockito.mock(OrderRepository::class.java)
//    private val paymentRepository = Mockito.mock(PaymentRepository::class.java)
//
//    private val id = UUID.randomUUID()
//    private fun orderMock(): Order {
//        return Order(
//            UUID.randomUUID(), 0, UUID.randomUUID(), UUID.fromString("00000000-0000-0000-0000-000000000000"), Date()
//        )
//    }
//
//    private class userMock : UserDetails {
//        override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
//            TODO("Not yet implemented")
//        }
//
//        override fun getPassword(): String = "string"
//
//        override fun getUsername(): String = "string"
//
//        override fun isAccountNonExpired(): Boolean = true
//
//        override fun isAccountNonLocked(): Boolean = true
//
//        override fun isCredentialsNonExpired(): Boolean = true
//
//        override fun isEnabled(): Boolean = true
//    }
//
//    private fun createService() : OrderService {
//        return DefaultOrderService(orderRepository, paymentRepository,  EventBus(), Mockito.mock(DefaultUserService::class.java))
//    }
//
//    @Test
//    fun getOrdersByUsername() {
//
//        val service = createService()
//        Mockito.`when`(orderRepository.findAll()).thenReturn(mutableListOf(orderMock()))
//
//        val actual = service.getOrdersByUsername(userMock())
//        val expected = listOf(orderMock().toModel())
//        Assert.assertEquals(actual, expected)
//    }
//
//    @Test
//    fun deleteOrder() {
//        val service = createService()
//        val order = orderMock()
//        Mockito.`when`(orderRepository.findByIdOrNull(Mockito.any())).thenReturn(order)
//        order.id?.let { service.deleteOrder(it, userMock()) }
//        Assert.assertEquals(4, order.status)
//    }
//}

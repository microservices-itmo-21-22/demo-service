package com.itmo.microservices.demo.order

import com.itmo.microservices.demo.order.api.model.BusketModel
import com.itmo.microservices.demo.order.api.model.ProductType
import com.itmo.microservices.demo.order.impl.entity.Busket
import com.itmo.microservices.demo.order.impl.entity.OrderItem
import com.itmo.microservices.demo.order.impl.repository.BusketRepository
import com.itmo.microservices.demo.order.impl.repository.OrderProductRepository
import com.itmo.microservices.demo.order.impl.util.toModel
import com.itmo.microservices.demo.tasks.impl.service.BusketServiceImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.*
import org.springframework.security.core.userdetails.UserDetails
import java.util.*


class BusketTest {
    private val productRepo = Mockito.mock(OrderProductRepository::class.java)
    private val busketRepo = Mockito.mock(BusketRepository::class.java)

    private val productId = UUID.randomUUID()
    private fun productMock(): OrderItem {
        return OrderItem(
            name = "Галоши",
            description = "Крутые галоши",
            country = "Русские",
            price = 300.0,
            sale = null,
            type = ProductType.CLOTHES
        ).also { it.id = productId }
    }

    private val busketId = UUID.randomUUID()
    private fun busketMock(): Busket {
        return Busket(
            "Govnoslav",
            mutableListOf(productMock())
        ).also { it.id = busketId }
    }

    @Test
    fun allBusketsTest() {
        val service = BusketServiceImpl(productRepo, busketRepo)
        Mockito.`when`(busketRepo.findAll()).thenReturn(mutableListOf(busketMock()))

        val actual = service.allBuskets()
        val expected = listOf(busketMock().toModel())
        Assertions.assertEquals(actual, expected)
    }

    @Test
    fun createBusketTest() {
        val service = BusketServiceImpl(productRepo, busketRepo)
        val user = Mockito.mock(UserDetails::class.java)
        Mockito.`when`(user.username).thenReturn("Govnoslav")
        Mockito.`when`(productRepo.findById(Mockito.any())).thenReturn(Optional.of(productMock()))

        val inputModel = BusketModel(null, listOf(productId), null, null)
        val actual = service.createBusket(inputModel, user)
        val expected = BusketModel(null, listOf(productId), user.username, null)
        Assertions.assertEquals(actual, expected)
    }

    @Test
    fun getBusketByIdTest() {
        val service = BusketServiceImpl(productRepo, busketRepo)
        Mockito.`when`(busketRepo.findById(Mockito.any())).thenReturn(Optional.ofNullable(busketMock()))
        val actual = service.getBusketById(busketId)
        val expected = busketMock().toModel()
        Assertions.assertEquals(actual, expected)
    }

    @Test
    fun deleteBusketByIdTest() {
        val service = BusketServiceImpl(productRepo, busketRepo)
        Mockito.`when`(busketRepo.findById(Mockito.any())).thenReturn(Optional.ofNullable(busketMock()))
        val actual = service.deleteBusketById(busketId)
        val expected = busketMock().toModel()
        Assertions.assertEquals(actual, expected)
    }

    @Test
    fun addProductToBusketTest() {
        val service = BusketServiceImpl(productRepo, busketRepo)
        Mockito.`when`(productRepo.findById(Mockito.any())).thenReturn(Optional.ofNullable(productMock()))
        Mockito.`when`(busketRepo.findById(Mockito.any())).thenReturn(Optional.ofNullable(busketMock().also { it.items = mutableListOf() }))
        val actual = service.addProductToBusket(busketId, productId)
        val expected = busketMock().toModel()
        Assertions.assertEquals(actual, expected)
    }

    @Test
    fun deleteProductFromBusketTest() {
        val service = BusketServiceImpl(productRepo, busketRepo)
        Mockito.`when`(productRepo.findById(Mockito.any())).thenReturn(Optional.ofNullable(productMock()))
        Mockito.`when`(busketRepo.findById(Mockito.any())).thenReturn(Optional.ofNullable(busketMock()))

        val actual = service.deleteProductFromBusket(busketId, productId)
        val expected = busketMock().also { it.items = mutableListOf() }.toModel()
        Assertions.assertEquals(expected, actual)
    }
}
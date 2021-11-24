package com.itmo.microservices.demo.stock.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.demo.stock.api.model.Category
import com.itmo.microservices.demo.stock.impl.entity.StockItem
import com.itmo.microservices.demo.stock.impl.repository.StockItemRepository
import com.itmo.microservices.demo.stock.impl.util.toModel
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito
import java.util.*

internal class DefaultStockItemServiceTest {


    private val repository = Mockito.mock(StockItemRepository::class.java)

    private val id = UUID.randomUUID()
    private fun itemMock(): StockItem {
        return StockItem(
            id = UUID.randomUUID(),
            name = "chocolate",
            price = 23.33,
            totalCount = 56,
            reservedCount = 1,
            category = Category.FOOD
        ).also { it.id = id }
    }

    @Test
    fun allStockItems() {
        val service = DefaultStockItemService(repository, eventBus = EventBus())
        Mockito.`when`(repository.findAll()).thenReturn(mutableListOf(itemMock()))

        val actual = service.allStockItems()
        val expected = listOf(itemMock().toModel())
        assertEquals(actual, expected)
    }

    @Test
    fun getStockItemById() {
        val service = DefaultStockItemService(repository, eventBus = EventBus())
        Mockito.`when`(repository.findById(Mockito.any())).thenReturn(Optional.ofNullable(itemMock()))
        val actual = service.getStockItemById(id)
        val expected = itemMock().toModel()
        assertEquals(actual, expected)
    }

    @Test
    fun deleteStockItem() {
        val service = DefaultStockItemService(repository, eventBus = EventBus())
        Mockito.`when`(repository.findById(Mockito.any())).thenReturn(Optional.ofNullable(itemMock()))
        val actual = service.deleteStockItemById(id)
        val expected = itemMock().toModel()
        assertEquals(actual, expected)
    }

    /* @Test
    fun createStockItem() {
        val service = DefaultStockItemService(repository, eventBus = EventBus())
        //Mockito.`when`(repository.findAll()).thenReturn(mutableListOf(itemMock()))

        val expected = itemMock().toModel()
        println(expected)
        service.createStockItem(expected)
        val actual = repository.getById(id)

        assertEquals(actual, expected)
    }*/
}
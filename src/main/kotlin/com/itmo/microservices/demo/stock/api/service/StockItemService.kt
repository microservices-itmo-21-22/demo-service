package com.itmo.microservices.demo.stock.api.service

import com.itmo.microservices.demo.stock.api.model.StockItemModel
import com.itmo.microservices.demo.stock.impl.entity.StockItem
import java.util.*

interface StockItemService {
    fun allStockItems(): List<StockItemModel>
    fun createStockItem(stockItem: StockItemModel)
    fun getStockItemById(stockItemId: UUID) : StockItemModel
    fun addStockItem(stockItemId: UUID, number: Int)
    fun reserveStockItem(stockItemId: UUID, number: Int) : Boolean
    fun deleteStockItemById(stockItemId: UUID)
    fun changeStockItem(stockItemId: UUID, stockItem: StockItemModel)
}

package com.itmo.microservices.demo.stock.api.service

import com.itmo.microservices.demo.stock.api.model.StockItemModel
import java.util.*

interface StockItemService {
    fun allStockItems(): List<StockItemModel>
    fun getStockItemById(stockItemId: UUID): StockItemModel
    fun addStockItem(stockItem: StockItemModel, number: Int)
    fun reserveStockItem(stockItemId: UUID, number: Int)
    fun deleteStockItemById(stockItemId: UUID, number: Int)
    fun changeStockItem(stockItemId: UUID, stockItem: StockItemModel)
}
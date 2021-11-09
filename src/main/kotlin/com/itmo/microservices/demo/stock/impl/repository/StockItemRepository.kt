package com.itmo.microservices.demo.stock.impl.repository

import com.itmo.microservices.demo.stock.impl.entity.StockItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface StockItemRepository : JpaRepository<StockItem, UUID> {

}
package com.itmo.microservices.demo.order.api.service

import com.itmo.microservices.demo.order.api.model.BusketModel
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

interface BusketService {
    fun allBuskets(): List<BusketModel>
    fun getBusketById(busketId: UUID): BusketModel
    fun deleteBusketById(busketId: UUID)
    fun createBusket(busket: BusketModel, author: UserDetails)
    fun addProductToBusket(busketId: UUID, productId: UUID)
    fun deleteProductFromBusket(busketId: UUID, productId: UUID)
}
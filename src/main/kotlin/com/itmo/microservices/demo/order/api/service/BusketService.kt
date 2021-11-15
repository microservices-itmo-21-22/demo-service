package com.itmo.microservices.demo.order.api.service

import com.itmo.microservices.demo.order.api.model.BusketDto
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

interface BusketService {
    fun allBuskets(): List<BusketDto>
    fun getBusketById(busketId: UUID): BusketDto
    fun deleteBusketById(busketId: UUID): BusketDto
    fun createBusket(busket: BusketDto, author: UserDetails): BusketDto
    fun addProductToBusket(busketId: UUID, productId: UUID): BusketDto
    fun deleteProductFromBusket(busketId: UUID, productId: UUID): BusketDto?
}
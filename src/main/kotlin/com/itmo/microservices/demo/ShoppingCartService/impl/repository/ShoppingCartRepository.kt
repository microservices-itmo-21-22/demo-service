package com.itmo.microservices.demo.ShoppingCartService.impl.repository

import com.itmo.microservices.demo.ShoppingCartService.impl.entity.ShoppingCart
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ShoppingCartRepository : JpaRepository<ShoppingCart, UUID>{
}
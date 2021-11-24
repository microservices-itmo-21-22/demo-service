package com.itmo.microservices.demo.shoppingCartService.impl.repository

import com.itmo.microservices.demo.shoppingCartService.impl.entity.ShoppingCartItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ShoppingCartItemRepository : JpaRepository<ShoppingCartItem, UUID> {
    fun findAllByShoppingCartID(shoppingCartID: UUID): List<ShoppingCartItem>
}
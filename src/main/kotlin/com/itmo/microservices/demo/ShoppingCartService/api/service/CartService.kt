package com.itmo.microservices.demo.ShoppingCartService.api.service

import com.itmo.microservices.demo.ShoppingCartService.impl.dto.CatalogItemDTO
import com.itmo.microservices.demo.ShoppingCartService.impl.dto.ShoppingCartDTO
import com.itmo.microservices.demo.ShoppingCartService.impl.entity.CatalogItem
import java.util.*

interface CartService {
    fun getCartItems(cartId: UUID) : List<CatalogItemDTO>
    fun getCart(cartId: UUID) : ShoppingCartDTO?
    fun getCatalogItem(catalogItemId: UUID) : CatalogItemDTO?
    fun makeCart() : ShoppingCartDTO?
    fun putItemInCart(cartId: UUID, catalogItem: UUID)
    fun makeCatalogItem(productId: UUID, amount: Int) : CatalogItemDTO?
}
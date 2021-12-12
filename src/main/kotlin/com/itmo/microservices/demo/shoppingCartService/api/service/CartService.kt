package com.itmo.microservices.demo.shoppingCartService.api.service

import com.itmo.microservices.demo.shoppingCartService.api.model.CatalogItemDTO
import com.itmo.microservices.demo.shoppingCartService.api.model.ShoppingCartDTO
import java.util.*

interface CartService {
    fun getCartItems(cartId: UUID) : List<CatalogItemDTO>
    fun getCart(cartId: UUID) : ShoppingCartDTO?
    fun getCatalogItem(catalogItemId: UUID) : CatalogItemDTO?
    fun makeCart() : ShoppingCartDTO?
    fun putItemInCart(cartId: UUID, catalogItem: UUID, amount: Int)
    fun makeCatalogItem(productId: UUID, amount: Int) : CatalogItemDTO?
}
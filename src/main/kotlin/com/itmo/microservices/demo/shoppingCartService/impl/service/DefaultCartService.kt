package com.itmo.microservices.demo.shoppingCartService.impl.service

import com.itmo.microservices.demo.shoppingCartService.api.service.CartService
import com.itmo.microservices.demo.shoppingCartService.impl.ShoppingCartStatus
import com.itmo.microservices.demo.shoppingCartService.api.model.CatalogItemDTO
import com.itmo.microservices.demo.shoppingCartService.api.model.ShoppingCartDTO
import com.itmo.microservices.demo.shoppingCartService.impl.entity.CatalogItem
import com.itmo.microservices.demo.shoppingCartService.impl.entity.ShoppingCart
import com.itmo.microservices.demo.shoppingCartService.impl.repository.CatalogItemRepository
import com.itmo.microservices.demo.shoppingCartService.impl.repository.ShoppingCartItemRepository
import com.itmo.microservices.demo.shoppingCartService.impl.repository.ShoppingCartRepository
import com.itmo.microservices.demo.stock.impl.repository.StockItemRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class DefaultCartService(private val shoppingCartRepository: ShoppingCartRepository, private val cartItemRepository: ShoppingCartItemRepository, private val catalogItemRepository: CatalogItemRepository, private val stockItemRepository: StockItemRepository) : CartService {

    override fun getCartItems(cartId: UUID): List<CatalogItemDTO> {
        val cartItems = cartItemRepository.findAllByShoppingCartID(cartId)
        val catalogItems : MutableList<CatalogItemDTO> = mutableListOf()

        cartItems.forEach {

            val catalogItem = catalogItemRepository.getById(it.catalogItemID)
            val catalogItemDTO = CatalogItemDTO(catalogItem.id, catalogItem.productId, catalogItem.amount)

            catalogItems.add(catalogItemDTO)
        }
        return catalogItems
    }

    override fun getCart(cartId: UUID): ShoppingCartDTO? {
        val shoppingCart = shoppingCartRepository.getById(cartId)

        val items = getCartItems(shoppingCart.id)

        return ShoppingCartDTO(shoppingCart.id, shoppingCart.status, items)
    }

    override fun getCatalogItem(catalogItemId: UUID): CatalogItemDTO? {
        var catalogItem = catalogItemRepository.findByIdOrNull(catalogItemId)
        if (catalogItem == null) {
            val stockItem = stockItemRepository.findByIdOrNull(catalogItemId)
            if (stockItem != null){
                catalogItem = CatalogItem(stockItem.id, stockItem.id, stockItem.amount)
                }
            else {
                return null
            }
        }
        return CatalogItemDTO(catalogItem.id, catalogItem.productId, catalogItem.amount)
    }

    override fun makeCart(): ShoppingCartDTO? {
        val cart = ShoppingCart(ShoppingCartStatus.active())

        shoppingCartRepository.save(cart)

        return getCart(cart.id)
    }

    override fun putItemInCart(cartId: UUID, catalogItemId: UUID, amount: Int) {
        val shoppingCartDTO = getCart(cartId)
        val catalogItemDTO = getCatalogItem(catalogItemId)

        if (shoppingCartDTO == null || catalogItemDTO == null) {
            return
        }

        if (shoppingCartDTO.items.contains(catalogItemDTO)) {
            if ( shoppingCartDTO.items[shoppingCartDTO.items.indexOf(catalogItemDTO)].amount != null) {
                shoppingCartDTO.items[shoppingCartDTO.items.indexOf(catalogItemDTO)].amount?.plus(amount)
            }
            else{
                shoppingCartDTO.items[shoppingCartDTO.items.indexOf(catalogItemDTO)].amount = amount
            }
        }
        else {
            val items = shoppingCartDTO.items.toMutableList()

            items.add(catalogItemDTO)

            shoppingCartDTO.items = items
        }
    }

    override fun makeCatalogItem(productId: UUID, amount: Int): CatalogItemDTO? {
        val catalogItem = CatalogItem(productId, productId, amount)

        catalogItemRepository.save(catalogItem)

        return CatalogItemDTO(catalogItem.id, catalogItem.productId, catalogItem.amount)
    }
}
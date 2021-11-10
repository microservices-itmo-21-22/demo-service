package com.itmo.microservices.demo.shoppingCartService.impl.service

import com.itmo.microservices.demo.shoppingCartService.api.service.CartService
import com.itmo.microservices.demo.shoppingCartService.impl.ShoppingCartStatus
import com.itmo.microservices.demo.shoppingCartService.api.dto.CatalogItemDTO
import com.itmo.microservices.demo.shoppingCartService.api.dto.ShoppingCartDTO
import com.itmo.microservices.demo.shoppingCartService.impl.entity.CatalogItem
import com.itmo.microservices.demo.shoppingCartService.impl.entity.ShoppingCart
import com.itmo.microservices.demo.shoppingCartService.impl.repository.CatalogItemRepository
import com.itmo.microservices.demo.shoppingCartService.impl.repository.ShoppingCartItemRepository
import com.itmo.microservices.demo.shoppingCartService.impl.repository.ShoppingCartRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class DefaultCartService(private val shoppingCartRepository: ShoppingCartRepository, private val cartItemRepository: ShoppingCartItemRepository, private val catalogItemRepository: CatalogItemRepository) : CartService {

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
//        val shoppingCart = ShoppingCart(ShoppingCartStatus.active())
//
//        val items : List<CatalogItemDTO> = listOf()

        return ShoppingCartDTO(shoppingCart.id, shoppingCart.status, items)
    }

    override fun getCatalogItem(catalogItemId: UUID): CatalogItemDTO? {
//        val catalogItem = catalogItemRepository.getById(catalogItemId)
        val catalogItem = CatalogItem(UUID.randomUUID(), 123)

        return CatalogItemDTO(catalogItem.id, catalogItem.productId, catalogItem.amount)
    }

    override fun makeCart(): ShoppingCartDTO? {
        val cart = ShoppingCart(ShoppingCartStatus.active())

        shoppingCartRepository.save(cart)

//        return getCart(cart.id)
        return ShoppingCartDTO(cart.id, cart.status, listOf())
    }

    override fun putItemInCart(cartId: UUID, catalogItemId: UUID) {
        val shoppingCartDTO = getCart(cartId)
        val catalogItemDTO = getCatalogItem(catalogItemId)

        if (shoppingCartDTO == null || catalogItemDTO == null) {
            return
        }

        if (shoppingCartDTO.items.contains(catalogItemDTO)) {
            shoppingCartDTO.items[shoppingCartDTO.items.indexOf(catalogItemDTO)].amount += catalogItemDTO.amount
        }
        else {
            val items = shoppingCartDTO.items.toMutableList()

            items.add(catalogItemDTO)

            shoppingCartDTO.items = items
        }
//        shoppingCartRepository.deleteById(cartId)
//        shoppingCartRepository.save(Shoppin)
        /// @todo put in repo
    }

    override fun makeCatalogItem(productId: UUID, amount: Int): CatalogItemDTO? {
        val catalogItem = CatalogItem(productId, amount)

//        catalogItemRepository.save(catalogItem)

        return CatalogItemDTO(catalogItem.id, catalogItem.productId, catalogItem.amount)
    }
}
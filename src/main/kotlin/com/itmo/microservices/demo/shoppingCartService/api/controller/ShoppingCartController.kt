package com.itmo.microservices.demo.shoppingCartService.api.controller

import com.itmo.microservices.demo.shoppingCartService.api.model.CatalogItemDTO
import com.itmo.microservices.demo.shoppingCartService.api.model.ShoppingCartDTO
import com.itmo.microservices.demo.shoppingCartService.impl.service.DefaultCartService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("user/{userId}/basket")
class ShoppingCartController (private val CartService: DefaultCartService){

    @GetMapping("/{cartID}")
    @Operation(
        summary = "Gets last shopping cart or creates a new one",
        responses = [
            ApiResponse(description = "OK", responseCode = "200", content = [Content()]),
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getCart(@PathVariable cartId: UUID): ShoppingCartDTO? = CartService.getCart(cartId)

    @PostMapping("/{cartID}/item")
    @Operation(
        summary = "Creates a new DTO object to put in cart",
        responses = [
            ApiResponse(description = "OK", responseCode = "200", content = [Content()]),
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun createCartItem(@RequestBody itemId: UUID): CatalogItemDTO? = CartService.makeCatalogItem(UUID.randomUUID(), 100)

    @GetMapping("/{cartID}/put/{catalogItemId}")
    @Operation(
        summary = "Adds a new CatalogItem to the Cart",
        responses = [
            ApiResponse(description = "OK", responseCode = "200", content = [Content()]),
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun putCartItemInCart(@PathVariable cartId: UUID, @PathVariable catalogItemId: UUID) = CartService.putItemInCart(cartId, catalogItemId)

    @GetMapping("/create_cart")
    @Operation(
        summary = "Creates a new cart",
        responses = [
            ApiResponse(description = "OK", responseCode = "200", content = [Content()]),
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun createCart(): ShoppingCartDTO? = CartService.makeCart()
}
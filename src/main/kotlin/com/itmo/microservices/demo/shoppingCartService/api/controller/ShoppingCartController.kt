package com.itmo.microservices.demo.shoppingCartService.api.controller

import com.itmo.microservices.demo.shoppingCartService.api.dto.CatalogItemDTO
import com.itmo.microservices.demo.shoppingCartService.api.dto.ShoppingCartDTO
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
    fun getCart(@PathVariable("cartID") cartID : UUID): ShoppingCartDTO? = CartService.getCart(cartID)

    @PostMapping("/{cartID}/item")
    @Operation(
            summary = "Creates a new DTO object to put in cart",
            responses = [
                ApiResponse(description = "OK", responseCode = "200", content = [Content()]),
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun createCartItem(@RequestBody cartID: UUID): CatalogItemDTO? = CartService.makeCatalogItem(UUID.randomUUID(), 100)

    @GetMapping("/{cartID}/put/{catalogItemId}")
    @Operation(
            summary = "Adds a new CatalogItem to the Cart",
            responses = [
                ApiResponse(description = "OK", responseCode = "200", content = [Content()]),
            ],
            security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun putCartItemInCart(@PathVariable cartID: UUID, @PathVariable catalogItemId: UUID) = CartService.putItemInCart(cartID, catalogItemId)

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
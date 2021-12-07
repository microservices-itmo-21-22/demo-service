package com.itmo.microservices.demo.products.api.controller



import com.itmo.microservices.demo.products.api.model.ProductModel
import com.itmo.microservices.demo.products.api.service.ProductsService
import com.itmo.microservices.demo.products.impl.entity.Product
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController

class ProductsController(private val productsService: ProductsService) {
    @RequestMapping("/items",method = [RequestMethod.GET])
    @Operation(
        summary = "Get product catalog / get all products",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getProductCatalog(@AuthenticationPrincipal @RequestParam available:Boolean):List<Product> = productsService.getAllProducts(available)

    @RequestMapping("/item",method = [RequestMethod.GET])
    @Operation(
        summary = "Get product information by id",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getProductInfoById(@AuthenticationPrincipal @RequestParam id:String):ProductModel = productsService.getProductInfoById(id)
}
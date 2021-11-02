package com.itmo.microservices.demo.products.api.controller

import com.itmo.microservices.demo.products.api.model.AddProductRequest
import com.itmo.microservices.demo.products.api.model.AddProductTypeRequest
import com.itmo.microservices.demo.products.api.model.CatalogModel
import com.itmo.microservices.demo.products.api.service.ProductsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductsController(private val productsService: ProductsService) {
    @GetMapping("/catalog")
    @Operation(
        summary = "Get product catalog / get all products",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
        ]
    )
    fun getProductCatalog():CatalogModel = productsService.getAllProducts()


    @PostMapping("/add/product")
    @Operation(
        summary = "add new product",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
        ]
    )
    fun addProduct(@RequestBody request: AddProductRequest) = productsService.addProduct(request)


}
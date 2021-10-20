package com.itmo.microservices.demo.categories.api.controller

import com.itmo.microservices.demo.categories.api.model.CategoryModel
import com.itmo.microservices.demo.categories.api.service.CategoryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/categories")
class CategoryController(private val categoryService: CategoryService) {

    @GetMapping
    @Operation(
        summary = "Get all categories",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun allCategories(): List<CategoryModel> = categoryService.allCategories()

    @GetMapping("/{categoryId}")
    @Operation(
        summary = "Get category by id",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()]),
            ApiResponse(description = "Category not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getCategoryById(@PathVariable categoryId: UUID): CategoryModel = categoryService.getCategoryById(categoryId)

    @PostMapping
    @Operation(
        summary = "Create category",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun addCategory(@RequestBody stockItem: CategoryModel) =
        categoryService.addCategory(stockItem)

    @DeleteMapping("/{categoryId}")
    @Operation(
        summary = "Delete category",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(
                description = "Unauthorized",
                responseCode = "403",
                content = [Content()]
            )
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun deleteCategoryById(@PathVariable categoryId: UUID) =
        categoryService.deleteCategoryById(categoryId)
}
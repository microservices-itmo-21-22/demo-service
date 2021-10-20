package com.itmo.microservices.demo.categories.api.service

import com.itmo.microservices.demo.categories.api.model.CategoryModel
import java.util.*

interface CategoryService {
    fun allCategories(): List<CategoryModel>
    fun getCategoryById(categoryId: UUID): CategoryModel
    fun addCategory(stockItem: CategoryModel)
    fun deleteCategoryById(categoryId: UUID)
}
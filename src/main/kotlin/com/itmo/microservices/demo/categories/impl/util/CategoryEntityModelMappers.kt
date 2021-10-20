package com.itmo.microservices.demo.categories.impl.util

import com.itmo.microservices.demo.categories.api.model.CategoryModel
import com.itmo.microservices.demo.categories.impl.entity.Category

fun CategoryModel.toEntity(): Category = Category(
    id = this.id,
    name = this.name
)

fun Category.toModel(): CategoryModel = CategoryModel(
    id = this.id,
    name = this.name
)

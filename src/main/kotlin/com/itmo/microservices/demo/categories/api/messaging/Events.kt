package com.itmo.microservices.demo.categories.api.messaging

import com.itmo.microservices.demo.categories.api.model.CategoryModel

data class CategoryCreatedEvent(val task: CategoryModel)

data class CategoryDeletedEvent(val task: CategoryModel)
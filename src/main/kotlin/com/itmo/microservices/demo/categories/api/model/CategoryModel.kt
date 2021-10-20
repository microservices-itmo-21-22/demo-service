package com.itmo.microservices.demo.categories.api.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CategoryModel(
    val id: UUID?,
    val name: String?)
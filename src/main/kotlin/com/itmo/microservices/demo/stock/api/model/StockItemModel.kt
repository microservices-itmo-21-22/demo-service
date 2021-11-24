package com.itmo.microservices.demo.stock.api.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.itmo.microservices.demo.tasks.api.model.TaskStatus
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class StockItemModel(
    val id: UUID,
    val name: String?,
    val price: Double?,
    val totalCount: Int?,
    val reservedCount: Int?,
    val category: Category = Category.COMMON)

package com.itmo.microservices.demo.tasks.api.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TaskDto(
        val id: UUID?,
        val author: String?,
        val assignee: String?,
        val title: String,
        val description: String?,
        val status: TaskStatusEnum = TaskStatusEnum.TODO)

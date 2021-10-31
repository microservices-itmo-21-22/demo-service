package com.itmo.microservices.demo.tasks.api.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TaskModel(
        val id: UUID?,
        val author: String?,
        val assignee: String?,
        val title: String,
        val description: String?,
        val status: TaskStatus = TaskStatus.TODO)
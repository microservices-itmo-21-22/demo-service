package com.itmo.microservices.demo.tasks.api.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TaskModel(
        val author: String?,
        val assignee: String?,
        val title: String,
        val description: String?,
        val status: TaskStatus = TaskStatus.TODO)
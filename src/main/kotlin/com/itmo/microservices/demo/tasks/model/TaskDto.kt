package com.itmo.microservices.demo.tasks.model

data class TaskDto(
        val author: String?,
        val assignee: String?,
        val title: String,
        val description: String?,
        val status: TaskStatus = TaskStatus.TODO)
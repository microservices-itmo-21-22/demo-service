package com.itmo.microservices.demo.tasks.impl.util

import com.itmo.microservices.demo.tasks.api.model.TaskDto
import com.itmo.microservices.demo.tasks.impl.entity.Task

fun TaskDto.toEntity(): Task = Task(
    id = this.id,
    author = this.author,
    assignee = this.assignee,
    title = this.title,
    description = this.description,
    status = this.status
)

fun Task.toModel(): TaskDto = TaskDto(
    id = this.id,
    author = this.author,
    assignee = this.assignee,
    title = this.title!!,
    description = this.description,
    status = this.status
)

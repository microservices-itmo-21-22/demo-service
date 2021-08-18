package com.itmo.microservices.demo.tasks.impl.util

import com.itmo.microservices.demo.tasks.api.model.TaskModel
import com.itmo.microservices.demo.tasks.impl.entity.Task

fun TaskModel.toEntity(): Task = Task(
    id = this.id,
    author = this.author,
    assignee = this.assignee,
    title = this.title,
    description = this.description,
    status = this.status
)

fun Task.toModel(): TaskModel = TaskModel(
    id = this.id,
    author = this.author,
    assignee = this.assignee,
    title = this.title!!,
    description = this.description,
    status = this.status
)

package com.itmo.microservices.demo.tasks.impl.util

import com.itmo.microservices.demo.lib.common.tasks.dto.TaskDto
import com.itmo.microservices.demo.lib.common.tasks.entity.TaskEntity

fun TaskDto.toEntity(): TaskEntity = TaskEntity(
    id = this.id,
    author = this.author,
    assignee = this.assignee,
    title = this.title,
    description = this.description,
    status = this.status
)

fun TaskEntity.toModel(): TaskDto = TaskDto(
    id = this.id,
    author = this.author,
    assignee = this.assignee,
    title = this.title!!,
    description = this.description,
    status = this.status
)

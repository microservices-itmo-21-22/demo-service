package com.itmo.microservices.demo.tasks.api.messaging

import com.itmo.microservices.demo.tasks.api.model.TaskDto

data class TaskCreatedEvent(val task: TaskDto)

data class TaskAssignedEvent(val task: TaskDto)

data class TaskDeletedEvent(val task: TaskDto)

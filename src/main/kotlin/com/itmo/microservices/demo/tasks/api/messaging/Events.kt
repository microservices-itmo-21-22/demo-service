package com.itmo.microservices.demo.tasks.api.messaging

import com.itmo.microservices.demo.tasks.api.model.TaskModel

data class TaskCreatedEvent(val task: TaskModel)

data class TaskAssignedEvent(val task: TaskModel)

data class TaskDeletedEvent(val task: TaskModel)

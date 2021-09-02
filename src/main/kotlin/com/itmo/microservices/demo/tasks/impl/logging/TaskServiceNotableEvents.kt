package com.itmo.microservices.demo.tasks.impl.logging

import com.itmo.microservices.commonlib.logging.NotableEvent

enum class TaskServiceNotableEvents(private val template: String) : NotableEvent {
    I_TASK_CREATED("Task created: {}"),
    I_TASK_ASSIGNED("Task assigned: {}"),
    I_TASK_DELETED("Task deleted: {}");

    override fun getTemplate(): String {
        return template
    }

    override fun getName(): String {
        return name
    }
}
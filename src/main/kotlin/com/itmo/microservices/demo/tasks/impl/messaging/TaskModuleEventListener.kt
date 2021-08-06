package com.itmo.microservices.demo.tasks.impl.messaging

import com.google.common.eventbus.Subscribe
import com.itmo.microservices.demo.tasks.impl.repository.TaskRepository
import com.itmo.microservices.demo.users.api.messaging.UserDeletedEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
@Suppress("UnstableApiUsage")
class TaskModuleEventListener(private val taskRepository: TaskRepository) {

    companion object {
        val log: Logger = LoggerFactory.getLogger(TaskModuleEventListener::class.java)
    }

    @Subscribe
    fun accept(event: UserDeletedEvent) {
        taskRepository.deleteAllByAuthor(event.username)
        log.info("All tasks authored by deleted user ${event.username} are deleted")
    }
}

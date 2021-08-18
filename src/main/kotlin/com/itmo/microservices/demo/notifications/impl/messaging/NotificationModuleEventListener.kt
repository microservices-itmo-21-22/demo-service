package com.itmo.microservices.demo.notifications.impl.messaging

import com.google.common.eventbus.AllowConcurrentEvents
import com.google.common.eventbus.Subscribe
import com.itmo.microservices.demo.notifications.api.service.NotificationService
import com.itmo.microservices.demo.tasks.api.messaging.TaskAssignedEvent
import com.itmo.microservices.demo.users.api.messaging.UserCreatedEvent
import org.springframework.stereotype.Component
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Component
@Suppress("UnstableApiUsage")
class NotificationModuleEventListener(private val notificationService: NotificationService) {

    private val executor: ExecutorService = Executors.newFixedThreadPool(5)

    @Subscribe
    @AllowConcurrentEvents
    fun accept(event: UserCreatedEvent) = executor.execute {
        notificationService.processNewUser(event.user)
    }

    @Subscribe
    @AllowConcurrentEvents
    fun accept(event: TaskAssignedEvent) = executor.execute {
        notificationService.processAssignedTask(event.task)
    }
}
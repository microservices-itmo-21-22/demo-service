package com.itmo.microservices.demo.notifications.impl.messaging

import com.google.common.eventbus.Subscribe
import com.itmo.microservices.demo.notifications.api.service.NotificationService
import com.itmo.microservices.demo.tasks.api.messaging.TaskAssignedEvent
import com.itmo.microservices.demo.users.api.messaging.UserCreatedEvent
import org.springframework.stereotype.Component

@Component
@Suppress("UnstableApiUsage")
class NotificationModuleEventListener(private val notificationService: NotificationService) {

    @Subscribe
    fun accept(event: UserCreatedEvent) = notificationService.processNewUser(event.user)

    @Subscribe
    fun accept(event: TaskAssignedEvent) = notificationService.processAssignedTask(event.task)
}
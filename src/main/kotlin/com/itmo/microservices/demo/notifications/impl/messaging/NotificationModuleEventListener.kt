package com.itmo.microservices.demo.notifications.impl.messaging

import com.google.common.eventbus.AllowConcurrentEvents
import com.google.common.eventbus.Subscribe
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.logging.CommonNotableEvents
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
    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    @Subscribe
    @AllowConcurrentEvents
    fun accept(event: UserCreatedEvent) = executor.execute {
        notificationService.processNewUser(event.user)
        eventLogger.info(
            CommonNotableEvents.I_LISTENER_RECEIVED_MESSAGE,
            event
        )
    }

    @Subscribe
    @AllowConcurrentEvents
    fun accept(event: TaskAssignedEvent) = executor.execute {
        notificationService.processAssignedTask(event.task)
        eventLogger.info(
            CommonNotableEvents.I_LISTENER_RECEIVED_MESSAGE,
            event
        )
    }
}
package com.itmo.microservices.demo.tasks.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.AccessDeniedException
import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.tasks.api.messaging.TaskAssignedEvent
import com.itmo.microservices.demo.tasks.api.messaging.TaskCreatedEvent
import com.itmo.microservices.demo.tasks.api.messaging.TaskDeletedEvent
import com.itmo.microservices.demo.tasks.api.model.TaskModel
import com.itmo.microservices.demo.tasks.api.service.TaskService
import com.itmo.microservices.demo.tasks.impl.logging.TaskServiceNotableEvents
import com.itmo.microservices.demo.tasks.impl.util.toEntity
import com.itmo.microservices.demo.tasks.impl.util.toModel
import com.itmo.microservices.demo.tasks.impl.repository.TaskRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
@Suppress("UnstableApiUsage")
class DefaultTaskService(private val taskRepository: TaskRepository,
                         private val eventBus: EventBus
                         ) : TaskService {

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override fun allTasks(): List<TaskModel> = taskRepository.findAll()
            .map { it.toModel() }

    override fun getTaskById(taskId: UUID): TaskModel =
            taskRepository.findByIdOrNull(taskId)?.toModel()
                    ?: throw NotFoundException("Task $taskId not found")

    override fun addTask(task: TaskModel, author: UserDetails) {
        val entity = task.toEntity().also { it.author = author.username }
        taskRepository.save(entity)
        eventBus.post(TaskCreatedEvent(entity.toModel()))
        eventLogger.info(
            TaskServiceNotableEvents.I_TASK_CREATED,
            entity
        )
    }

    override fun assignTask(taskId: UUID, username: String, requester: UserDetails) {
        val task = taskRepository.findByIdOrNull(taskId) ?: throw NotFoundException("Task $taskId not found")
        if (task.author != requester.username)
            throw AccessDeniedException("Cannot change task that was not created by you")
        task.assignee = username
        taskRepository.save(task)
        eventBus.post(TaskAssignedEvent(task.toModel()))
        eventLogger.info(
            TaskServiceNotableEvents.I_TASK_ASSIGNED,
            task
        )
    }

    override fun deleteTaskById(taskId: UUID, requester: UserDetails) {
        val task = taskRepository.findByIdOrNull(taskId) ?: return
        if (task.author != requester.username)
            throw AccessDeniedException("Cannot change task that was not created by you")
        taskRepository.deleteById(taskId)
        eventBus.post(TaskDeletedEvent(task.toModel()))
        eventLogger.info(
            TaskServiceNotableEvents.I_TASK_DELETED,
            task
        )
    }
}
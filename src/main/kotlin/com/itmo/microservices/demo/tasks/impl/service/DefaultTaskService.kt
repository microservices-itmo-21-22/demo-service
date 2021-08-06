package com.itmo.microservices.demo.tasks.impl.service

import com.itmo.microservices.demo.common.exception.AccessDeniedException
import com.itmo.microservices.demo.common.exception.NotFoundException
import com.itmo.microservices.demo.tasks.api.model.TaskModel
import com.itmo.microservices.demo.tasks.api.service.TaskService
import com.itmo.microservices.demo.tasks.impl.entity.Task
import com.itmo.microservices.demo.tasks.impl.repository.TaskRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class DefaultTaskService(private val taskRepository: TaskRepository) : TaskService {

    override fun allTasks(): List<TaskModel> = taskRepository.findAll()
            .map { entityToModel(it) }

    override fun getTaskById(taskId: UUID): TaskModel =
            taskRepository.findByIdOrNull(taskId)?.let { entityToModel(it) }
                    ?: throw NotFoundException("Task $taskId not found")

    override fun addTask(task: TaskModel, author: UserDetails) {
        taskRepository.save(modelToEntity(task).also { it.author = author.username })
    }

    override fun assignTask(taskId: UUID, username: String, requester: UserDetails) {
        val task = taskRepository.findByIdOrNull(taskId) ?: throw NotFoundException("Task $taskId not found")
        if (task.author != requester.username)
            throw AccessDeniedException("Cannot change task that was not created by you")
        task.assignee = username
        taskRepository.save(task)
    }

    override fun deleteTaskById(taskId: UUID, requester: UserDetails) {
        val task = taskRepository.findByIdOrNull(taskId) ?: return
        if (task.author != requester.username)
            throw AccessDeniedException("Cannot change task that was not created by you")
        taskRepository.deleteById(taskId)
    }

    private fun entityToModel(entity: Task): TaskModel = TaskModel(
            author = entity.author,
            assignee = entity.assignee,
            title = entity.title!!,
            description = entity.description,
            status = entity.status
    )

    private fun modelToEntity(model: TaskModel): Task = Task(
            author = model.author,
            assignee = model.assignee,
            title = model.title,
            description = model.description,
            status = model.status
    )
}
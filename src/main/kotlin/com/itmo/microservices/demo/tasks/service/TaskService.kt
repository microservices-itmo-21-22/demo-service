package com.itmo.microservices.demo.tasks.service

import com.itmo.microservices.demo.exception.NotFoundException
import com.itmo.microservices.demo.tasks.entity.Task
import lombok.AllArgsConstructor
import com.itmo.microservices.demo.tasks.repository.TaskRepository
import java.util.UUID
import com.itmo.microservices.demo.tasks.model.TaskDto
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
@AllArgsConstructor
class TaskService(private val taskRepository: TaskRepository) {

    fun allTasks(): List<Task> = taskRepository.findAll().filterNotNull()

    fun getTaskById(taskId: UUID): Task {
        return taskRepository.findByIdOrNull(taskId) ?: throw NotFoundException("Task $taskId not found")
    }

    fun addTask(task: TaskDto) {
        //TODO 04.08.2021 Ilya_Kuznetsov: pass author as AuthenticationPrincipal from controller
        val entity = Task(author=task.author, assignee=task.assignee, title = task.title, description = task.description, status = task.status)
        taskRepository.save(entity)
    }

    fun assignTask(taskId: UUID, username: String) {
        val task = taskRepository.findByIdOrNull(taskId) ?: throw NotFoundException("Task $taskId not found")
        task.assignee = username
        taskRepository.save(task)
    }

    fun deleteTaskById(taskId: UUID) = taskRepository.deleteById(taskId)
}
package com.itmo.microservices.demo.tasks.api.service

import com.itmo.microservices.demo.tasks.api.model.TaskModel
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

interface TaskService {
    fun allTasks(): List<TaskModel>
    fun getTaskById(taskId: UUID): TaskModel
    fun addTask(task: TaskModel, author: UserDetails)
    fun assignTask(taskId: UUID, username: String, requester: UserDetails)
    fun deleteTaskById(taskId: UUID, requester: UserDetails)
}
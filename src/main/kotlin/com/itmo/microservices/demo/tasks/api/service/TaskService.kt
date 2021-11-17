package com.itmo.microservices.demo.tasks.api.service

import com.itmo.microservices.demo.lib.common.tasks.dto.TaskDto
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

interface TaskService {
    fun allTasks(): List<TaskDto>
    fun getTaskById(taskId: UUID): TaskDto
    fun addTask(task: TaskDto, author: UserDetails)
    fun assignTask(taskId: UUID, username: String, requester: UserDetails)
    fun deleteTaskById(taskId: UUID, requester: UserDetails)
}

package com.itmo.microservices.demo.tasks.api.controller

import com.itmo.microservices.demo.tasks.api.model.TaskModel
import com.itmo.microservices.demo.tasks.api.service.TaskService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController(private val taskService: TaskService) {

    @GetMapping
    fun allTasks(): List<TaskModel> = taskService.allTasks()

    @GetMapping("/{taskId}")
    fun getTaskById(@PathVariable taskId: UUID): TaskModel = taskService.getTaskById(taskId)

    @PostMapping
    fun addTask(@RequestBody task: TaskModel,
                @AuthenticationPrincipal author: UserDetails) = taskService.addTask(task, author)

    @PostMapping("/{taskId}/assignee/{username}")
    fun assignTask(@PathVariable taskId: UUID,
                   @PathVariable username: String,
                   @AuthenticationPrincipal requester: UserDetails) =
            taskService.assignTask(taskId, username, requester)

    @DeleteMapping("/{taskId}")
    fun deleteTaskById(@PathVariable taskId: UUID,
                       @AuthenticationPrincipal requester: UserDetails) =
            taskService.deleteTaskById(taskId, requester)
}
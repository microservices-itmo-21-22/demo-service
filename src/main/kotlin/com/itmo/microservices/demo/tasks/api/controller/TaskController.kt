package com.itmo.microservices.demo.tasks.api.controller

import com.itmo.microservices.demo.tasks.api.model.TaskModel
import com.itmo.microservices.demo.tasks.api.service.TaskService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController(private val taskService: TaskService) {

    @GetMapping
    @Operation(summary = "Get all tasks", security = [SecurityRequirement(name = "bearerAuth")])
    fun allTasks(): List<TaskModel> = taskService.allTasks()

    @GetMapping("/{taskId}")
    @Operation(summary = "Get task by id", security = [SecurityRequirement(name = "bearerAuth")])
    fun getTaskById(@PathVariable taskId: UUID): TaskModel = taskService.getTaskById(taskId)

    @PostMapping
    @Operation(summary = "Create task", security = [SecurityRequirement(name = "bearerAuth")])
    fun addTask(@RequestBody task: TaskModel,
                @AuthenticationPrincipal author: UserDetails) = taskService.addTask(task, author)

    @PostMapping("/{taskId}/assignee/{username}")
    @Operation(summary = "Assign task", security = [SecurityRequirement(name = "bearerAuth")])
    fun assignTask(@PathVariable taskId: UUID,
                   @PathVariable username: String,
                   @AuthenticationPrincipal requester: UserDetails) =
            taskService.assignTask(taskId, username, requester)

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete task", security = [SecurityRequirement(name = "bearerAuth")])
    fun deleteTaskById(@PathVariable taskId: UUID,
                       @AuthenticationPrincipal requester: UserDetails) =
            taskService.deleteTaskById(taskId, requester)
}
package com.itmo.microservices.demo.tasks.api.controller

import com.itmo.microservices.demo.tasks.api.model.TaskModel
import com.itmo.microservices.demo.tasks.api.service.TaskService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController(private val taskService: TaskService) {

    @GetMapping
    @Operation(
        summary = "Get all tasks",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun allTasks(): List<TaskModel> = taskService.allTasks()

    @GetMapping("/{taskId}")
    @Operation(
        summary = "Get task by id",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()]),
            ApiResponse(description = "Task not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getTaskById(@PathVariable taskId: UUID): TaskModel = taskService.getTaskById(taskId)

    @PostMapping
    @Operation(
        summary = "Create task",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun addTask(@RequestBody task: TaskModel,
                @Parameter(hidden = true) @AuthenticationPrincipal author: UserDetails) =
        taskService.addTask(task, author)

    @PostMapping("/{taskId}/assignee/{username}")
    @Operation(
        summary = "Assign task",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(
                description = "Unauthorized or task was not created by you",
                responseCode = "403",
                content = [Content()]
            ),
            ApiResponse(description = "Task not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun assignTask(@PathVariable taskId: UUID,
                   @PathVariable username: String,
                   @Parameter(hidden = true) @AuthenticationPrincipal requester: UserDetails) =
            taskService.assignTask(taskId, username, requester)

    @DeleteMapping("/{taskId}")
    @Operation(
        summary = "Delete task",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(
                description = "Unauthorized or task was not created by you",
                responseCode = "403",
                content = [Content()]
            )
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun deleteTaskById(@PathVariable taskId: UUID,
                       @Parameter(hidden = true) @AuthenticationPrincipal requester: UserDetails) =
            taskService.deleteTaskById(taskId, requester)
}
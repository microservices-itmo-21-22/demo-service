package com.itmo.microservices.demo.tasks.controller

import com.itmo.microservices.demo.tasks.entity.Task
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import lombok.AllArgsConstructor
import com.itmo.microservices.demo.tasks.service.TaskService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.UUID
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import com.itmo.microservices.demo.tasks.model.TaskDto
import org.springframework.web.bind.annotation.DeleteMapping

@RestController
@RequestMapping("/tasks")
@AllArgsConstructor
class TaskController(private val taskService: TaskService) {

    @GetMapping
    fun allTasks(): List<Task> = taskService.allTasks()

    @GetMapping("/{taskId}")
    fun getTaskById(@PathVariable taskId: UUID): Task = taskService.getTaskById(taskId)

    @PostMapping
    fun addTask(@RequestBody task: TaskDto) = taskService.addTask(task)

    @PostMapping("/{taskId}/assignee/{username}")
    fun assignTask(@PathVariable taskId: UUID, @PathVariable username: String) =
            taskService.assignTask(taskId, username)

    @DeleteMapping("/{taskId}")
    fun deleteTaskById(@PathVariable taskId: UUID) = taskService.deleteTaskById(taskId)
}
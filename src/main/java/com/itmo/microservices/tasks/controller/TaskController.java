package com.itmo.microservices.tasks.controller;

import com.itmo.microservices.tasks.entity.Task;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.itmo.microservices.tasks.model.TaskDto;
import com.itmo.microservices.tasks.service.TaskService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{taskId}")
    public Task getTaskById(@PathVariable UUID taskId) {
        return taskService.getTaskById(taskId);
    }

    @PostMapping
    public void addTask(@RequestBody TaskDto task) {
        taskService.addTask(task);
    }

    @PostMapping("/{taskId}/assignee/{username}")
    public void assignTask(@PathVariable UUID taskId, @PathVariable String username) {
        taskService.assignTask(taskId, username);
    }

    @DeleteMapping("/{taskId}")
    public void deleteTaskById(@PathVariable UUID taskId) {
        taskService.deleteTaskById(taskId);
    }
}

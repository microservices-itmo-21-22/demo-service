package com.itmo.microservices.tasks.service;

import com.itmo.microservices.common.exception.NotFoundException;
import com.itmo.microservices.tasks.entity.Task;
import com.itmo.microservices.tasks.model.TaskDto;
import com.itmo.microservices.tasks.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(UUID taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task " + taskId + " not found"));
    }

    public void addTask(TaskDto task) {
        taskRepository.save(
                Task.builder()
                        .author(task.getAuthor())
                        .assignee(task.getAssignee())
                        .description(task.getDescription())
                        .status(task.getStatus())
                        .build()
        );
    }

    public void assignTask(UUID taskId, String username) {
        Task task = taskRepository
                .findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task " + taskId + " not found"));
        task.setAssignee(username);
        taskRepository.save(task);
    }

    public void deleteTaskById(UUID taskId) {
        taskRepository.deleteById(taskId);
    }
}

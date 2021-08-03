package com.itmo.microservices.tasks.repository;

import com.itmo.microservices.tasks.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
}

package com.itmo.microservices.tasks.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TaskDto {
    String author;
    String assignee;
    String description;
    TaskStatus status;
}

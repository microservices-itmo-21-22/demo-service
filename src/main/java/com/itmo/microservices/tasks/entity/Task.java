package com.itmo.microservices.tasks.entity;

import com.itmo.microservices.tasks.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue
    private UUID id;
    private String author;
    private String assignee;
    private String description;
    private TaskStatus status;

    public void start() {
        this.status = TaskStatus.IN_PROCESS;
    }

    public void complete() {
        this.status = TaskStatus.DONE;
    }
}

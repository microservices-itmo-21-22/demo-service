package com.itmo.microservices.demo.lib.common.tasks.repository

import com.itmo.microservices.demo.lib.common.tasks.entity.TaskEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TaskRepository : JpaRepository<TaskEntity, UUID> {
    fun deleteAllByAuthor(author: String)
}

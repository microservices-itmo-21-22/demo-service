package com.itmo.microservices.demo.lib.common.tasks.entity

import com.itmo.microservices.demo.lib.common.tasks.dto.TaskStatusEnum
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class TaskEntity {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    var id: UUID? = null
    var author: String? = null
    var assignee: String? = null
    var title: String? = null
    var description: String? = null
    var status: TaskStatusEnum = TaskStatusEnum.TODO

    constructor()

    constructor(id: UUID? = null, author: String? = null, assignee: String? = null, title: String, description: String? = null, status: TaskStatusEnum) {
        this.id = id
        this.author = author
        this.assignee = assignee
        this.title = title
        this.description = description
        this.status = status
    }

    override fun toString(): String =
            "Task(id=$id, author=$author, assignee=$assignee, title=$title, description=$description, status=$status)"

}

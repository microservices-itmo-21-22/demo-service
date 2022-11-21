package com.itmo.microservices.demo.notifications.api.service

import com.itmo.microservices.demo.tasks.api.model.TaskModel
import com.itmo.microservices.demo.users.api.model.UserDTO

interface NotificationService {
    fun processNewUser(user: UserDTO)
    fun processAssignedTask(task: TaskModel)
}
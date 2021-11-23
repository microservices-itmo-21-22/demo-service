package com.itmo.microservices.demo.notifications.api.service

import com.itmo.microservices.demo.lib.common.tasks.dto.TaskDto
import com.itmo.microservices.demo.users.api.model.AppUserModel

interface NotificationService {
    fun processNewUser(user: AppUserModel)
    fun processAssignedTask(task: TaskDto)
}

package com.itmo.microservices.demo.notifications.api.service

import com.itmo.microservices.demo.users.api.model.UserModel

interface NotificationService {
    fun processNewUser(user: UserModel)
}
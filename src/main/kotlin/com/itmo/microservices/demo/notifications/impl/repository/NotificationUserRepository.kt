package com.itmo.microservices.demo.notifications.impl.repository

import com.itmo.microservices.demo.notifications.impl.entity.NotificationUser
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationUserRepository: JpaRepository<NotificationUser, String>
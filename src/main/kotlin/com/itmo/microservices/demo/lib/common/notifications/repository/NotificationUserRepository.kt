package com.itmo.microservices.demo.lib.common.notifications.repository

import com.itmo.microservices.demo.lib.common.notifications.entity.NotificationUser
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationUserRepository: JpaRepository<NotificationUser, String>

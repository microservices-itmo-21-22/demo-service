package com.itmo.microservices.demo.users.api.messaging

import com.itmo.microservices.demo.users.api.model.UserModel

data class UserCreatedEvent(val user: UserModel)

data class UserDeletedEvent(val username: String)

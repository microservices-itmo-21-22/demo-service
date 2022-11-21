package com.itmo.microservices.demo.users.api.messaging

import com.itmo.microservices.demo.users.api.model.UserDTO

data class UserCreatedEvent(val user: UserDTO)

data class UserDeletedEvent(val username: String)

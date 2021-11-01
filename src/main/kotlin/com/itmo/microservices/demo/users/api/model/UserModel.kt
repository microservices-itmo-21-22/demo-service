package com.itmo.microservices.demo.users.api.model

import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails

data class UserModel(
    val name: String,
    val password: String,
    val status: Status
) {
    fun userDetails(): UserDetails = User(name, password, emptyList())
}
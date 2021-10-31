package com.itmo.microservices.demo.users.api.model

data class UserRequestDto(
    val name: String,
    val password: String
)
package com.itmo.microservices.demo.users.model

data class RegistrationRequest(
        val username: String,
        val name: String,
        val surname: String,
        val email: String,
        val password: String
)
package com.itmo.microservices.demo.users.api.model

data class RegistrationRequest(
        val username: String,
        val name: String,
        val surname: String,
        val email: String,
        val password: String
)
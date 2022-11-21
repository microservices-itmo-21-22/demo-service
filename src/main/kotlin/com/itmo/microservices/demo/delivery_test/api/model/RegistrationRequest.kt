package com.itmo.microservices.demo.users.api.model

data class RegistrationRequest(
        val name: String,
        val password: String
)
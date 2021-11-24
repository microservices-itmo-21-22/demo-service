package com.itmo.microservices.demo.users.api.model

import java.util.*
import javax.persistence.Id

data class RegistrationRequest(
        val id: UUID,
        val ipaddress: String,
        val username: String,
        val name: String,
        val email: String,
        val password: String,
        val phone: String
)
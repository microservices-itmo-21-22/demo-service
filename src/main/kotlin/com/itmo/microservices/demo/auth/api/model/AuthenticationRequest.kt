package com.itmo.microservices.demo.auth.api.model

import java.util.*

data class AuthenticationRequest(val id: UUID?, val name: String, val password: String)

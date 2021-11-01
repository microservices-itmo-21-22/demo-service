package com.itmo.microservices.demo.users.api.model

data class AuthenticationResult(val accessToken: String, val refreshToken: String)

package com.itmo.microservices.demo.auth.api.model

data class AuthenticationResult(val accessToken: String, val refreshToken: String)

package com.itmo.microservices.demo.auth.model

data class AuthenticationResult(val accessToken: String, val refreshToken: String)

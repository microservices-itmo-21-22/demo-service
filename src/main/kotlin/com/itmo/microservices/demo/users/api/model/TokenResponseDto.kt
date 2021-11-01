package com.itmo.microservices.demo.users.api.model

data class TokenResponseDto(
    val accessToken: String,
    val refreshToken: String
)

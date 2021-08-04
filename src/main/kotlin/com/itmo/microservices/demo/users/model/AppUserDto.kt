package com.itmo.microservices.demo.users.model

data class AppUserDto(
        val username: String,
        val name: String,
        val surname: String,
        val email: String)

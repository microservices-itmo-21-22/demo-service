package com.itmo.microservices.demo.users.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails

data class AppUserModel(
        val name: String,
        @JsonIgnore
        val password: String) {

        fun userDetails(): UserDetails = User(name, password, emptyList())
}

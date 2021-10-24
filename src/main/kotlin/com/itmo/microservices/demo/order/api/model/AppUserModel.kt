package com.itmo.microservices.demo.order.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.itmo.microservices.demo.order.impl.entity.Busket
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails

data class AppUserModel(
        val username: String,
        val name: String,
        val surname: String,
        val email: String,
        @JsonIgnore
        val password: String,
        val buskets: List<Busket>?) {

    fun userDetails(): UserDetails = User(username, password, emptyList())
}
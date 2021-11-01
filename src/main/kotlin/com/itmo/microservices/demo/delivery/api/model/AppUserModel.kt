package com.itmo.microservices.demo.delivery.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.itmo.microservices.demo.delivery.impl.entity.DeliveryPayment
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails

data class AppUserModel(
    val username: String,
    val name: String,
    val surname: String,
    val email: String,
    @JsonIgnore
    val password: String,
    val payments: List<DeliveryPayment>?
) {

    fun userDetails(): UserDetails = User(username, password, emptyList())
}
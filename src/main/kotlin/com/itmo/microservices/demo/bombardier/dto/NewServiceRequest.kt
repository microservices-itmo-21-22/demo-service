package com.itmo.microservices.demo.bombardier.dto

data class NewServiceRequest(
    val name: String,
    val url: String
)

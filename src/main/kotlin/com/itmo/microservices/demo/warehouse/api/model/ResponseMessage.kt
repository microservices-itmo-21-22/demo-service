package com.itmo.microservices.demo.warehouse.api.model

enum class ResponseMessage(private val data: String) {
    OK_CREATED("{\"code\":200,\"message\":\"Item was created\"}"),
    OK_UPDATED("{\"code\":200,\"message\":\"Item was updated\"}"),
    BAD_REQUEST(
        "{\"code\":400,\"error\":\"Request is invalid\"}"),
    BAD_NOT_FOUND("{\"code\":400,\"error\":\"Item not found\"}"),
    BAD_QUANTITY("{\"code\":400,\"error\":\"Not enough quantity\"}");

    fun getText(): String {
        return data
    }
}
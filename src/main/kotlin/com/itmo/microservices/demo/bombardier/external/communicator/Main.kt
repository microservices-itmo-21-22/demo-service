package com.itmo.microservices.demo.bombardier.external.communicator

import com.itmo.microservices.demo.bombardier.external.communicator.ExternalServiceApiCommunicator.Companion.toRequestBody
import com.shopify.promises.Promise
import com.shopify.promises.then
import org.springframework.boot.configurationprocessor.json.JSONObject
import java.net.URL
import java.util.*

fun main() {
    val communicator = ExternalServiceApiCommunicator(URL("http://localhost:8080/"))
    var token: ExternalServiceToken? = null
    communicator.execute("/users") {
        JSONObject().apply {
            put("username", "user1")
            put("name", "user1")
            put("surname", "user1")
            put("email", "test@test.ru")
            put("password", "user1")

            post(toRequestBody())
        }
    }.then {
        communicator.authenticate("user1", "user1")
    }.then { it ->
        token = it
        communicator.executeWithAuth("/tasks", token!!) {
            val body = JSONObject().withItems(
                "id" to UUID.randomUUID().toString(),
                "author" to "user1",
                "assignee" to "user1",
                "title" to "Test task",
                "description" to "Descr",
                "status" to "TODO"
            )
            post(body.toRequestBody())
        }
    }
        .then {
            communicator.executeWithAuth("/tasks", token!!)
        }
        .whenComplete {
            when (it) {
                is Promise.Result.Success -> println(it.value.body()?.string())
                is Promise.Result.Error -> println("shit")
            }
        }


}
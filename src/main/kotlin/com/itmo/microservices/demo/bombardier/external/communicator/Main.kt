package com.itmo.microservices.demo.bombardier.external.communicator

import com.itmo.microservices.demo.bombardier.external.communicator.ExternalServiceApiCommunicator.Companion.toRequestBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.springframework.boot.configurationprocessor.json.JSONObject
import java.net.URL
import java.util.*
import java.util.concurrent.ForkJoinPool

suspend fun main() {
    val executor = ForkJoinPool(5)
    val communicator = UserAwareExternalServiceApiCommunicator(URL("http://77.234.215.138:30014/"), executor)
    CoroutineScope(executor.asCoroutineDispatcher()).launch {

        communicator.execute("/users") {
            JSONObject().apply {
                put("username", "user1")
                put("name", "user1")
                put("surname", "user1")
                put("email", "test@test.ru")
                put("password", "user1")

                post(toRequestBody())
            }
        }

        val token = communicator.authenticate("user1", "user1")

        communicator.executeWithAuth("/tasks", token) {
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

        println(communicator.executeWithAuth("/tasks", token).body()?.string())
    }.join()

}
package com.itmo.microservices.demo.warehouse

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.itmo.microservices.demo.users.api.model.AuthenticationRequest
import com.itmo.microservices.demo.users.api.model.UserRequestDto
import com.itmo.microservices.demo.warehouse.api.model.CatalogItemModel
import org.hamcrest.CoreMatchers
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension::class, MockitoExtension::class)
@ActiveProfiles("dev")
class WarehouseControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun executeWarehouseTest() {
        val accessToken = getTestUserAccessToken()
        val request = CatalogItemModel(
            "Test item",
            "It's test item",
            1000
        )

        mockMvc.post("/api/warehouse/addItem") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }.andReturn()

        mockMvc.get("/api/warehouse/getItems") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            content { jsonPath("$[0].title", CoreMatchers.`is`("Test item")) }
            content { jsonPath("$[0].description", CoreMatchers.`is`("It's test item")) }
            content { jsonPath("$[0].price", CoreMatchers.`is`(1000)) }
        }
    }

    private fun getTestUserAccessToken() : Any? {
        val request = UserRequestDto("user", "password")

        mockMvc.post("/users") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }

        val authRequest = AuthenticationRequest("user", "password")

        val result = mockMvc.post("/users/auth") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(authRequest)
            accept = MediaType.APPLICATION_JSON
        }.andReturn()

        return JSONObject(result.response.contentAsString).get("accessToken")
    }

    private fun getAccessToken(name: String, password: String): Any? {
        val authRequest = AuthenticationRequest(name, password)

        val result = mockMvc.post("/users/auth") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(authRequest)
            accept = MediaType.APPLICATION_JSON
        }.andReturn()

        return JSONObject(result.response.contentAsString).get("accessToken")
    }
}
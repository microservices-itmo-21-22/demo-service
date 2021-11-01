package com.itmo.microservices.demo.users

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.itmo.microservices.demo.users.api.model.AuthenticationRequest
import com.itmo.microservices.demo.users.api.model.UserRequestDto
import org.hamcrest.CoreMatchers.`is`
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
class UserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun addUserTest() {
        val request = UserRequestDto("addedUser", "addedPass")

        mockMvc.post("/users") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { jsonPath("$.name", `is`("addedUser")) }
        }
    }

    @Test
    fun authUserTest() {
        addTestUser("authTestUser", "authTestPass")
        val authRequest = AuthenticationRequest("authTestUser", "authTestPass")

        mockMvc.post("/users/auth") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(authRequest)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }
    }

    @Test
    fun authUserTest_wrongPassword() {
        addTestUser("authTestFailUser", "authTestFailPass")
        val authRequest = AuthenticationRequest("authTestFailUser", "wrongPass")

        mockMvc.post("/users/auth") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(authRequest)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isEqualTo(403) }
        }
    }

    @Test
    fun refreshTokenTest() {
        addTestUser("refreshTokenTestUser", "rttPass")
        val authRequest = AuthenticationRequest("refreshTokenTestUser", "rttPass")
        val refreshToken = getRefreshToken("refreshTokenTestUser", "rttPass")

        mockMvc.post("/users/refresh") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $refreshToken")
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(authRequest)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }
    }

    @Test
    fun getUserTest() {
        val id = addTestUser("getUserTest", "getUserTestPass")
        val accessToken = getAccessToken("getUserTest", "getUserTestPass")

        mockMvc.get("/users/$id") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { jsonPath("$.name", `is`("getUserTest")) }
            content { jsonPath("$.id", `is`(id)) }
        }
    }

    @Test
    fun getUserTest_wrongUserID() {
        addTestUser("getUserFailTest", "getUserFailTestPass")
        val accessToken = getAccessToken("getUserFailTest", "getUserFailTestPass")

        mockMvc.get("/users/666") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        }.andExpect {
            status { isEqualTo(404) }
        }
    }

    private fun addTestUser(name: String, password: String): Any? {
        val request = UserRequestDto(name, password)

        val result = mockMvc.post("/users") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andReturn()

        return JSONObject(result.response.contentAsString).get("id")
    }

    private fun getRefreshToken(name: String, password: String): Any? {
        val authRequest = AuthenticationRequest(name, password)

        val result = mockMvc.post("/users/auth") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(authRequest)
            accept = MediaType.APPLICATION_JSON
        }.andReturn()

        return JSONObject(result.response.contentAsString).get("refreshToken")
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
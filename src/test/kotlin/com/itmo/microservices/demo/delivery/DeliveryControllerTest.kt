package com.itmo.microservices.demo.delivery

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.itmo.microservices.demo.delivery.api.model.DeliveryModel
import com.itmo.microservices.demo.delivery.api.model.DeliveryType
import com.itmo.microservices.demo.users.api.model.AuthenticationRequest
import com.itmo.microservices.demo.users.api.model.UserRequestDto
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
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.text.DateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension::class, MockitoExtension::class)
@ActiveProfiles("dev")
class DeliveryControllerTest {
//    @Autowired
//    private lateinit var mockMvc: MockMvc
//
//    @Test
//    fun addDeliveryTest() {
//        val accessToken = getAccessToken("user", "password")
//        val request = DeliveryModel(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa4"),
//            "user",
//            DeliveryType.COURIER,
//            0,
//            LocalDateTime.parse("2021-11-05T14:35:44"),
//            "address",
//            "courierCompany"
//        )
//
//        mockMvc.post("/delivery") {
//            header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
//            contentType = MediaType.APPLICATION_JSON
//            content = jacksonObjectMapper().writeValueAsString(request)
//            accept = MediaType.APPLICATION_JSON
//        }.andExpect {
//            status { isOk() }
//        }
//    }
//
//    @Test
//    fun getDeliveryTest() {
//        val accessToken = getTestUserAccessToken()
//        val request = DeliveryModel(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa4"),
//            "user",
//            DeliveryType.COURIER,
//            0,
//            LocalDateTime.parse("2021-11-05T14:35:44"),
//            "address",
//            "courierCompany"
//        )
//
//        mockMvc.post("/delivery") {
//            header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
//            contentType = MediaType.APPLICATION_JSON
//            content = jacksonObjectMapper().writeValueAsString(request)
//            accept = MediaType.APPLICATION_JSON
//        }
//
//        mockMvc.get("/delivery/${request.id}") {
//            header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
//        }.andExpect {
//            status { isOk() }
//            content { contentType(MediaType.APPLICATION_JSON) }
//            content { jsonPath("$.id", CoreMatchers.`is`("3fa85f64-5717-4562-b3fc-2c963f66afa4")) }
//            content { jsonPath("$.user", CoreMatchers.`is`("user")) }
//        }
//    }
//
//    @Test
//    fun deleteDeliveryTest() {
//        val accessToken = getAccessToken("user", "password")
//        val request = DeliveryModel(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa4"),
//            "user",
//            DeliveryType.COURIER,
//            0,
//            LocalDateTime.parse("2021-11-05T14:35:44"),
//            "address",
//            "courierCompany"
//        )
//
//        mockMvc.post("/delivery") {
//            header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
//            contentType = MediaType.APPLICATION_JSON
//            content = jacksonObjectMapper().writeValueAsString(request)
//            accept = MediaType.APPLICATION_JSON
//        }
//
//        mockMvc.delete("/delivery/${request.id}") {
//            header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
//        }.andExpect {
//            status { isOk() }
//        }
//    }
//
//    @Test
//    fun getAllDeliveriesTest() {
//        val accessToken = getAccessToken("user", "password")
//        val request1 = DeliveryModel(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa4"),
//            "user",
//            DeliveryType.COURIER,
//            0,
//            LocalDateTime.parse("2021-11-05T14:35:44"),
//            "address",
//            "courierCompany"
//        )
//        val request2 = DeliveryModel(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa3"),
//            "user",
//            DeliveryType.COURIER,
//            0,
//            LocalDateTime.parse("2021-11-05T14:35:44"),
//            "address",
//            "courierCompany"
//        )
//
//        mockMvc.post("/delivery") {
//            header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
//            contentType = MediaType.APPLICATION_JSON
//            content = jacksonObjectMapper().writeValueAsString(request1)
//            accept = MediaType.APPLICATION_JSON
//        }
//
//        mockMvc.post("/delivery") {
//            header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
//            contentType = MediaType.APPLICATION_JSON
//            content = jacksonObjectMapper().writeValueAsString(request2)
//            accept = MediaType.APPLICATION_JSON
//        }
//
//        mockMvc.get("/delivery/all") {
//            header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
//        }.andExpect {
//            status { isOk() }
//            content { jsonPath("$[0].id", CoreMatchers.`is`("3fa85f64-5717-4562-b3fc-2c963f66afa4")) }
//            content { jsonPath("$[1].id", CoreMatchers.`is`("3fa85f64-5717-4562-b3fc-2c963f66afa3")) }
//        }
//    }
//
//    private fun getTestUserAccessToken() : Any? {
//        val request = UserRequestDto("user", "password")
//
//        mockMvc.post("/users") {
//            contentType = MediaType.APPLICATION_JSON
//            content = jacksonObjectMapper().writeValueAsString(request)
//            accept = MediaType.APPLICATION_JSON
//        }
//
//        val authRequest = AuthenticationRequest("user", "password")
//
//        val result = mockMvc.post("/users/auth") {
//            contentType = MediaType.APPLICATION_JSON
//            content = jacksonObjectMapper().writeValueAsString(authRequest)
//            accept = MediaType.APPLICATION_JSON
//        }.andReturn()
//
//        return JSONObject(result.response.contentAsString).get("accessToken")
//    }
//
//    private fun getAccessToken(name: String, password: String): Any? {
//        val authRequest = AuthenticationRequest(name, password)
//
//        val result = mockMvc.post("/users/auth") {
//            contentType = MediaType.APPLICATION_JSON
//            content = jacksonObjectMapper().writeValueAsString(authRequest)
//            accept = MediaType.APPLICATION_JSON
//        }.andReturn()
//
//        return JSONObject(result.response.contentAsString).get("accessToken")
//    }
}
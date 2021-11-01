package com.itmo.microservices.demo.payment

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.itmo.microservices.demo.payment.api.model.PaymentRequestDto
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
import org.springframework.test.web.servlet.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension::class, MockitoExtension::class)
@ActiveProfiles("dev")
class PaymentControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun executePaymentTest() {
        val accessToken = getTestUserAccessToken()
        val paymentRequest = PaymentRequestDto(1)

        mockMvc.post("/payment/transaction") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(paymentRequest)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { jsonPath("$.transactionId", CoreMatchers.`is`(0)) }
        }
    }

    private fun getTestUserAccessToken() : Any? {
        val request = UserRequestDto("testUser", "testPass")

        mockMvc.post("/users") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }

        val authRequest = AuthenticationRequest("testUser", "testPass")

        val result = mockMvc.post("/users/auth") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(authRequest)
            accept = MediaType.APPLICATION_JSON
        }.andReturn()

        return JSONObject(result.response.contentAsString).get("accessToken")
    }
}
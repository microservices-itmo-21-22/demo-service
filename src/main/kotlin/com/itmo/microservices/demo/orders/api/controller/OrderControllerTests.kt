/*
package com.itmo.microservices.demo.orders.api.controller

import com.itmo.microservices.demo.DemoServiceApplication
import junit.framework.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

@ActiveProfiles("dev")
@SpringBootTest(classes = [DemoServiceApplication::class])
@AutoConfigureMockMvc
class OrderControllerTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun addProduct() {
        var mvcResult: MockHttpServletResponse = mockMvc.perform(
                MockMvcRequestBuilders.post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                 {}
                """.trimIndent())
                        .accept(MediaType.ALL)
        )
                .andReturn()
                .response
                .apply(::println)

        Assert.assertEquals(mvcResult.status, 200)
    }
}*/

package com.itmo.microservices.demo.products

import com.itmo.microservices.demo.DemoServiceApplication

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@ActiveProfiles("dev")
@SpringBootTest(classes = [DemoServiceApplication::class])
@AutoConfigureMockMvc
class ProductsControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun addProduct() {
        var mvcResult: MockHttpServletResponse = mockMvc.perform(
            MockMvcRequestBuilders.post("/products/add/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                 {
                    "name": "milk",
                    "description": "so delicious",
                    "country": "Russia",
                    "price": 300,
                    "sale": 400,
                    "type": "FOOD"
                 }
                """.trimIndent())
                .accept(MediaType.ALL)
        )
            .andReturn()
            .response
            .apply(::println)

        Assertions.assertEquals(mvcResult.status,200)
    }

    @Test
    fun getProductCatalog() {
        var mvcResult: MockHttpServletResponse = mockMvc.perform(
            MockMvcRequestBuilders.get("/products/catalog")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
        )
            .andReturn()
            .response
            .apply(::println)
        Assertions.assertEquals(mvcResult.status,200)
    }
}
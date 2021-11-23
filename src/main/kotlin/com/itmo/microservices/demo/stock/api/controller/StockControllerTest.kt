//package com.itmo.microservices.demo.stock.api.controller
//
//import com.itmo.microservices.demo.DemoServiceApplication
//import junit.framework.Assert.assertEquals
//
//
//import org.junit.Test
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.mock.web.MockHttpServletResponse
//import org.springframework.test.web.servlet.MockMvc
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
//import org.springframework.http.MediaType
//import org.springframework.test.context.ActiveProfiles
//
//@ActiveProfiles("dev")
//@SpringBootTest(classes = [DemoServiceApplication::class])
//@AutoConfigureMockMvc
//class StockItemControllerTest {
//    @Autowired
//    private lateinit var mockMvc: MockMvc
//
//    @Test
//    fun addProduct() {
//        var mvcResult: MockHttpServletResponse = mockMvc.perform(
//            MockMvcRequestBuilders.post("/items")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("""
//                 {
//                  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
//                  "name": "string",
//                  "price": 23.31,
//                  "totalCount": 67,
//                  "reservedCount": 3,
//                  "category": "COMMON"
//                }
//                """.trimIndent())
//                .accept(MediaType.ALL)
//        )
//            .andReturn()
//            .response
//            .apply(::println)
//
//        assertEquals(mvcResult.status,200)
//    }
//
//    @Test
//    fun getProductCatalog() {
//        var mvcResult: MockHttpServletResponse = mockMvc.perform(
//            MockMvcRequestBuilders.get("/items?available=true")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.ALL)
//        )
//            .andReturn()
//            .response
//            .apply(::println)
//        assertEquals(mvcResult.status,200)
//    }
//}

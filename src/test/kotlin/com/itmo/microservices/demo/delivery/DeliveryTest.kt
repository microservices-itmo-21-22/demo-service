package com.itmo.microservices.demo.delivery




import com.itmo.microservices.demo.DemoServiceApplication
import com.itmo.microservices.demo.delivery.api.service.DeliveryService
import com.itmo.microservices.demo.order.api.model.OrderDto
import junit.framework.Assert.assertEquals
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.lang.Thread.sleep
import java.util.*


@ActiveProfiles("dev")
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [DemoServiceApplication::class])
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.JVM)


class DeliveryTest{
//    @Autowired
//    lateinit var deliveryService: DeliveryService
//    @Autowired
//    private lateinit var mockMvc: MockMvc
//
//        var slot = 0
//
//        @Test
//        fun getSlots(){
//            var mvcResult: MockHttpServletResponse =mockMvc.perform(
//            MockMvcRequestBuilders.get("http://127.0.0.1:8080/delivery/slots?number=1")
//                .accept(MediaType.ALL)
//            ).andReturn().response.apply ( ::println )
//            println(mvcResult.status)
//            println(mvcResult.contentAsString)
//            assertEquals(mvcResult.status,200)
//            slot = mvcResult.contentAsString.substring(1,mvcResult.contentAsString.length-1).toInt()
//            println(slot)
//        }
//
//    @Test
//    fun delivery(){
//        var ID =UUID.randomUUID()
//        var order = OrderDto(id= ID, timeCreated = System.currentTimeMillis(),status = null,
//        itemsMap = null, deliveryDuration = 1000,paymentHistory = null)
//        deliveryService.delivery(order)
//        sleep( 1000*30)
//
//        var mvcResult: MockHttpServletResponse =mockMvc.perform(
//            MockMvcRequestBuilders.get("http://127.0.0.1:8080/_internal/deliveryLog/${ID}")
//                .accept(MediaType.ALL)
//        ).andReturn().response.apply ( ::println )
//        println(mvcResult.status)
//        println(mvcResult.contentAsString)
//        assertEquals(mvcResult.status,200)
//
//    }
}
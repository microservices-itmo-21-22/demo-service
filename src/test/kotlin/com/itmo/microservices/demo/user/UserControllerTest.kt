package com.itmo.microservices.demo.user



import com.itmo.microservices.demo.DemoServiceApplication
import com.itmo.microservices.demo.users.api.service.UserService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath


@ActiveProfiles("dev")
@SpringBootTest(classes = [DemoServiceApplication::class])
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    lateinit var userService: UserService
    @Autowired
    private lateinit var mockMvc: MockMvc

    var accessToken :String=""
    var userId:String=""
    val userName:String="test2"
    val password:String = "stringpwd"
    @Test
    fun register() {
        var mvcResult: MockHttpServletResponse =  mockMvc.perform(
            MockMvcRequestBuilders.post("http://127.0.0.1:8080/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        """
                            {
                              "name": "$userName",
                              "password": "$password"
                            }
                        """.trimIndent()
                        )
                .accept(MediaType.ALL)
        ).andDo(MockMvcResultHandlers.print())
            .andReturn()
            .getResponse()

        userId = mvcResult.contentAsString.substring(7,43)
        println(userId)

        println(mvcResult.getContentAsString())
        auth()
    }

    fun auth(){
        var mvcResult: MockHttpServletResponse = mockMvc.perform(
            MockMvcRequestBuilders.post("/authentication")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                              "username": "$userName",
                              "password": "$password"
                            }
                """.trimIndent()).accept(MediaType.APPLICATION_JSON)
        ).andReturn()
            .response
            .apply(::println)

        println(mvcResult.status)
        println(mvcResult.contentAsString)
        accessToken = mvcResult.contentAsString.substring(16,226)
        println(accessToken)
        getAccountData()

    }

    fun getAccountData() {

        var mvcResult: MockHttpServletResponse =mockMvc.perform(
            MockMvcRequestBuilders
                .get("/users/$userId")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .accept(MediaType.ALL)
        ).andExpect(jsonPath("$.name").value("$userName"))
            .andReturn()
            .response
            .apply(::println)
        println(mvcResult.status)
        Assertions.assertEquals(mvcResult.status,200)

    }



}
package com.itmo.microservices.demo.user



import com.itmo.microservices.demo.DemoServiceApplication
import com.itmo.microservices.demo.users.api.service.UserService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.ActiveProfiles
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders


@ActiveProfiles("dev")
@SpringBootTest(classes = [DemoServiceApplication::class])
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    lateinit var userService: UserService
    @Autowired
    private lateinit var mockMvc: MockMvc

    var accessToken :String=""

    val userName:String="test1"
    val password:String = "stringpwd"
    @Test
    fun register() {
          mockMvc.perform(
            MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        """
                            {
                              "username": "$userName",
                              "name": "testName",
                              "surname": "tt",
                              "email": "string@t.ru",
                              "password": "$password"
                            }
                        """.trimIndent()
                        )
                .accept(MediaType.ALL)
        ).andReturn()
            .response
            .apply(::println)
    }
    @Test
    fun auth(){
        var mvcResult: MockHttpServletResponse = mockMvc.perform(
            MockMvcRequestBuilders.post("/authentication")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                              "username": "$userName",
                              "password": "$password"
                            }
                """.trimIndent()) .accept(MediaType.APPLICATION_JSON)
        ).andReturn()
            .response
            .apply(::println)
        println(mvcResult.status)
        println(mvcResult.contentAsString)
        accessToken = mvcResult.contentAsString.substring(16,226)
        println(accessToken)
        getAccountData()
        deleteCurrentUser()
    }

    fun getAccountData() {
        var mvcResult: MockHttpServletResponse =mockMvc.perform(
            MockMvcRequestBuilders
                .get("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .accept(MediaType.ALL)
        ).andReturn()
            .response
            .apply(::println)

        Assertions.assertEquals(mvcResult.status,200)
        Assertions.assertEquals(mvcResult.contentAsString, """{"username":"test1","name":"testName","surname":"tt","email":"string@t.ru"}""")
    }


    fun deleteCurrentUser() {
        var mvcResult: MockHttpServletResponse =mockMvc.perform(
            MockMvcRequestBuilders.delete("/users/me")
                .header("Authorization", "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andReturn()
            .response
            .apply(::println)

        Assertions.assertEquals(mvcResult.status,200)
    }
}
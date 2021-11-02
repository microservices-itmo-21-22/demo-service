package com.itmo.microservices.demo.users.api



import com.itmo.microservices.demo.DemoServiceApplication

import com.itmo.microservices.demo.users.api.service.UserService
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


@ActiveProfiles("dev")
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [DemoServiceApplication::class])
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.JVM)
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
//        val om : ObjectMapper;
//        val re = RegistrationRequest(name = "rq",surname = "yy",username = "123",email = "123@145.com",password = "123");
//        val res= userService.registerUser(re)
//        print(res)


          mockMvc.perform(
            MockMvcRequestBuilders.post("http://127.0.0.1:8080/users")
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
            MockMvcRequestBuilders.post("http://127.0.0.1:8080/authentication")
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
                .get("http://127.0.0.1:8080/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .accept(MediaType.ALL)
        ).andReturn()
            .response
            .apply(::println)

        println(mvcResult.status)
        println(mvcResult.contentAsString)
        assert(mvcResult.status==200)
        assert(mvcResult.contentAsString=="""{"username":"test1","name":"testName","surname":"tt","email":"string@t.ru"}""")
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
        println(mvcResult.status)
        assert(mvcResult.status==200)
    }
}
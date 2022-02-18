package com.itmo.microservices.demo.user


import com.google.common.eventbus.EventBus

import com.itmo.microservices.demo.users.impl.entity.AppUser
import com.itmo.microservices.demo.users.impl.repository.UserRepository
import com.itmo.microservices.demo.users.impl.service.DefaultUserService
import com.itmo.microservices.demo.users.impl.util.toModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*


class UserTest {
    private val usersRepository = Mockito.mock(UserRepository::class.java)
    private val eventBus = Mockito.mock(EventBus::class.java)
    private val passwordEncoder = Mockito.mock(PasswordEncoder::class.java)
    private val usersId = UUID.randomUUID()

    var accessToken :String=""
    var userId = UUID.randomUUID()
    private val userName:String="test2"
    private val password:String = "stringpwd"

    private fun usersMock():AppUser{
        return AppUser(userName,password).also { it.id = usersId }
    }
    @Test
    fun getAccountDataTest() {
        val usersService = DefaultUserService(usersRepository,passwordEncoder,eventBus)
        Mockito.`when`(usersRepository.findById(usersId)).thenReturn(usersMock())
        val actual = usersService.getAccountData(null, usersId)
        val expected = usersMock().toModel()
        Assertions.assertEquals(actual, expected)
    }

}
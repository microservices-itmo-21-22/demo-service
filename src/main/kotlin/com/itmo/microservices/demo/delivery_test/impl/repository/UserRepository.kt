package com.itmo.microservices.demo.users.impl.repository

import org.springframework.data.jpa.repository.JpaRepository
import com.itmo.microservices.demo.users.impl.entity.User
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, String>{
    fun findAppUserByName(name: String): User
    fun findAppUserById(id: UUID): User?
}
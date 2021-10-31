package com.itmo.microservices.demo.users.impl.repository

import com.itmo.microservices.demo.users.impl.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Int> {
    fun findUserByName(name: String): User
}
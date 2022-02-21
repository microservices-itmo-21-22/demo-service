package com.itmo.microservices.demo.users.impl.repository

import org.springframework.data.jpa.repository.JpaRepository
import com.itmo.microservices.demo.users.impl.entity.AppUser
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<AppUser, String> {
    fun findByName(name: String):AppUser?
    fun findById(id: UUID):AppUser?
}
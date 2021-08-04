package com.itmo.microservices.demo.users.service

import com.itmo.microservices.demo.users.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {
}
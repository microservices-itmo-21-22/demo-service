package com.itmo.microservices.demo.payments.impl.repository

import com.itmo.microservices.demo.payments.impl.entity.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TransactionRepository: JpaRepository<Transaction, UUID>
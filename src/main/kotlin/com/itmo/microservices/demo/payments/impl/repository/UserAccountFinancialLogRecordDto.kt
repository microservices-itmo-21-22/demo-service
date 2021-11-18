package com.itmo.microservices.demo.payments.impl.repository

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserAccountFinancialLogRecordDto : JpaRepository<UserAccountFinancialLogRecordDto, UUID>
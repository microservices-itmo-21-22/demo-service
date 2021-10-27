package com.itmo.microservices.demo.payment.impl.repository;

import com.itmo.microservices.demo.payment.impl.model.UserAccountFinancialLogRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserAccountFinancialLogRecordRepository extends JpaRepository<UserAccountFinancialLogRecord, UUID> {

    List<UserAccountFinancialLogRecord> findAllByUserId(UUID userId);
}

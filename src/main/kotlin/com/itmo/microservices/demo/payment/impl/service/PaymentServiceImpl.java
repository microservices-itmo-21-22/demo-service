package com.itmo.microservices.demo.payment.impl.service;


import com.itmo.microservices.demo.payment.api.model.FinancialOperationType;
import com.itmo.microservices.demo.payment.api.model.PaymentSubmissionDto;
import com.itmo.microservices.demo.payment.api.model.UserAccountFinancialLogRecordDto;
import com.itmo.microservices.demo.payment.api.service.PaymentService;
import com.itmo.microservices.demo.payment.impl.model.UserAccountFinancialLogRecord;
import com.itmo.microservices.demo.payment.impl.repository.UserAccountFinancialLogRecordRepository;
import com.itmo.microservices.demo.payment.impl.utils.UserAccountFinancialLogRecordUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

    private final UserAccountFinancialLogRecordRepository userAccountFinancialLogRecordRepository;

    @PostConstruct
    public void init() {

    }

    @Override
    public List<UserAccountFinancialLogRecordDto> getFinlog(UUID userId) {
        userAccountFinancialLogRecordRepository.save(
                UserAccountFinancialLogRecord.builder()
                        .paymentTransactionId(UUID.randomUUID())
                        .amount(1)
                        .type(FinancialOperationType.REFUND)
                        .orderId(UUID.randomUUID())
                        .timestamp(LocalDateTime.now())
                        .userId(userId)
                        .build()
        );

        userAccountFinancialLogRecordRepository.save(
                UserAccountFinancialLogRecord.builder()
                        .paymentTransactionId(UUID.randomUUID())
                        .amount(1)
                        .type(FinancialOperationType.REFUND)
                        .orderId(UUID.randomUUID())
                        .timestamp(LocalDateTime.now())
                        .userId(userId)
                        .build()
        ); // temporary just to test

        var list = userAccountFinancialLogRecordRepository.findAllByUserId(userId);
        return list
                .stream()
                .map(UserAccountFinancialLogRecordUtils::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentSubmissionDto executeOrderPayment(UserDetails user, UUID orderId) {
        return PaymentSubmissionDto.builder()
                .timestamp(LocalDateTime.now())
                .transactionID(UUID.randomUUID()) //TODO:: query to order repo by orderId
                .build();
    }
}

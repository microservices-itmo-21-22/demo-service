package com.itmo.microservices.demo.payment.impl.service;


import com.itmo.microservices.demo.payment.PaymentServiceConstants;
import com.itmo.microservices.demo.payment.api.model.FinancialOperationType;
import com.itmo.microservices.demo.payment.api.model.PaymentSubmissionDto;
import com.itmo.microservices.demo.payment.api.model.UserAccountFinancialLogRecordDto;
import com.itmo.microservices.demo.payment.api.service.PaymentService;
import com.itmo.microservices.demo.payment.impl.model.UserAccountFinancialLogRecord;
import com.itmo.microservices.demo.payment.impl.repository.UserAccountFinancialLogRecordRepository;
import com.itmo.microservices.demo.payment.utils.UserAccountFinancialLogRecordUtils;
import com.itmo.microservices.demo.users.api.exception.UserNotFoundException;
import com.itmo.microservices.demo.users.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

    private final UserAccountFinancialLogRecordRepository userAccountFinancialLogRecordRepository;
    private final UserService userService;

    @Override
    public List<UserAccountFinancialLogRecordDto> getFinlog(String username, UUID orderId) throws UserNotFoundException {
        var user = userService.getUser(username);

        if (user == null) {
            throw new UserNotFoundException(String.format("%s user with username: '%s' not found",
                    PaymentServiceConstants.PAYMENT_LOG_MARKER, username));
        }

        userAccountFinancialLogRecordRepository.save(
                UserAccountFinancialLogRecord.builder()
                        .paymentTransactionId(UUID.randomUUID())
                        .amount(1)
                        .type(FinancialOperationType.REFUND)
                        .orderId(orderId != null ? orderId : UUID.randomUUID())
                        .timestamp(LocalDateTime.now())
                        .userId(user.getId())
                        .build()
        ); // temporary just to test

        var list = orderId != null ?
                userAccountFinancialLogRecordRepository.findAllByUserIdAndOrderId(user.getId(), orderId) :
                userAccountFinancialLogRecordRepository.findAllByUserId(user.getId()); //TODO:: criteria API? @Query?
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

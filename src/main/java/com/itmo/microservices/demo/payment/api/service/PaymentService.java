package com.itmo.microservices.demo.payment.api.service;

import com.itmo.microservices.demo.payment.api.model.PaymentSubmissionDto;
import com.itmo.microservices.demo.payment.api.model.UserAccountFinancialLogRecordDto;
import com.itmo.microservices.demo.users.api.exception.UserNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    List<UserAccountFinancialLogRecordDto> getFinlog(String username, UUID orderId) throws UserNotFoundException;
    PaymentSubmissionDto executeOrderPayment(UserDetails user, UUID orderId);
}

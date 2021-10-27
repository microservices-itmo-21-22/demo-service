package com.itmo.microservices.demo.payment.api.controller;

import com.itmo.microservices.demo.payment.api.model.PaymentSubmissionDto;
import com.itmo.microservices.demo.payment.api.model.UserAccountFinancialLogRecordDto;
import com.itmo.microservices.demo.payment.api.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/payment")
@RestController
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/finlog")
    List<UserAccountFinancialLogRecordDto> getFinlog(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(value = "order_id", required = false) UUID orderId
    ) {
        return paymentService.getFinlog(user.getUsername(), orderId);
    }

    @PostMapping
    PaymentSubmissionDto executeOrderPayment (
            @AuthenticationPrincipal UserDetails user,
            @RequestParam("order_id") UUID orderId
    ) {
        return paymentService.executeOrderPayment(user, orderId);
    }
}

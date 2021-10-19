package com.itmo.microservices.demo.payment.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSubmissionDto {
    private LocalDateTime timestamp; // в доках timestamp: Long, но это кринж, но я уточню
    private UUID transactionID;
}

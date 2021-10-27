package com.itmo.microservices.demo.payment.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLogRecordDto {
    private UUID transactionId;
    private LocalDateTime timestamp;
    private PaymentStatus status;
    private Integer amount;
}

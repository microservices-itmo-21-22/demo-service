package com.itmo.microservices.demo.payment.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
//@AllArgsConstructor
public class PaymentLogRecordDto {
    public UUID transactionId;
    public LocalDateTime timestamp;
    public PaymentStatus status;
    public Integer amount;

    public PaymentLogRecordDto(UUID _transactionId,
                        LocalDateTime _timestamp,
                        PaymentStatus _status,
                        Integer _amount)
    {
        transactionId = _transactionId;
        timestamp = _timestamp;
        status = _status;
        amount = _amount;
    }
}

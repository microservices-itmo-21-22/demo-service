package com.itmo.microservices.demo.payment.impl.service;

import com.itmo.microservices.demo.order.api.service.OrderService;
import io.prometheus.client.Counter;

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
    private final OrderService orderService;
    private final UserService userService;
    private static final String serviceName = "p03";

    @Override
    public List<UserAccountFinancialLogRecordDto> getFinlog(String name, UUID orderId) throws UserNotFoundException {
        var user = userService.getUser(name);

        if (user == null) {
            throw new UserNotFoundException(String.format("%s user with name: '%s' not found",
                    PaymentServiceConstants.PAYMENT_LOG_MARKER, name));
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

    static final Counter revenue =
            Counter.build().name("revenue_total").help("Total revenue").labelNames(serviceName).register();

    @Override
    public PaymentSubmissionDto executeOrderPayment(UserDetails user, UUID orderId) {
        var order = orderService.getOrder(orderId);
        if (order != null) {
            var itemsMap = order.getItemsMap();
            if (itemsMap != null) {
                itemsMap.forEach((orderItemDto, items_count) -> {
                    var price = orderItemDto.getPrice();
                    if (price != null)
                        revenue.inc(price * items_count);
                });
            }
        }
        return PaymentSubmissionDto.builder()
                .timestamp(LocalDateTime.now())
                .transactionID(UUID.randomUUID()) //TODO:: query to order repo by orderId
                .build();
    }
}

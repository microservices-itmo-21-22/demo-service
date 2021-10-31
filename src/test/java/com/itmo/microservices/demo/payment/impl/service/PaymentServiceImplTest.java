package com.itmo.microservices.demo.payment.impl.service;

import com.itmo.microservices.demo.payment.api.model.FinancialOperationType;
import com.itmo.microservices.demo.payment.impl.model.UserAccountFinancialLogRecord;
import com.itmo.microservices.demo.payment.impl.repository.UserAccountFinancialLogRecordRepository;
import com.itmo.microservices.demo.users.api.exception.UserNotFoundException;
import com.itmo.microservices.demo.users.api.service.UserService;
import com.itmo.microservices.demo.users.impl.entity.AppUser;
import com.itmo.microservices.demo.users.impl.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private UserAccountFinancialLogRecordRepository repository;

    @Mock
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    private final UUID id = UUID.randomUUID();

    private final List<UserAccountFinancialLogRecord> list = new ArrayList<UserAccountFinancialLogRecord>();

    private final AppUser user = new AppUser(
            "username",
            "name",
            "surname",
            "email",
            "password"
    );

    @Before
    public void setUp(){
        UserAccountFinancialLogRecord entity = UserAccountFinancialLogRecord.builder()
                .paymentTransactionId(UUID.randomUUID())
                .amount(1)
                .type(FinancialOperationType.REFUND)
                .orderId(id)
                .timestamp(LocalDateTime.now())
                .userId(user.getId())
                .build();
        when(userService.getUser("username")).thenReturn(user);
        when(repository.save(entity)).thenReturn(entity);
        when(repository.findAllByUserIdAndOrderId(user.getId(), id)).thenReturn(list);
        when(userDetails.getUsername()).thenReturn("username");
    }

    @Test
    void getFinlogTest() throws UserNotFoundException {
        Assert.assertEquals(list, paymentService.getFinlog("username", id));
    }

    @Test
    void executeOrderPaymentTest() {
        Assert.assertEquals(null, paymentService.executeOrderPayment(userDetails, id).toString());
    }
}
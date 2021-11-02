package com.itmo.microservices.demo.payment.impl.service;

import com.itmo.microservices.demo.payment.api.model.FinancialOperationType;
import com.itmo.microservices.demo.payment.api.model.PaymentSubmissionDto;
import com.itmo.microservices.demo.payment.impl.model.UserAccountFinancialLogRecord;
import com.itmo.microservices.demo.payment.impl.repository.UserAccountFinancialLogRecordRepository;
import com.itmo.microservices.demo.users.api.exception.UserNotFoundException;
import com.itmo.microservices.demo.users.api.service.UserService;
import com.itmo.microservices.demo.users.impl.entity.AppUser;
import com.itmo.microservices.demo.users.impl.repository.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.when;

@SuppressWarnings("UnstableApiUsage")
@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private UserAccountFinancialLogRecordRepository repository;

    @Mock
    UserRepository userRepository;

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
        when(userService.getUser("username")).thenReturn(user);
        UserAccountFinancialLogRecord entity = UserAccountFinancialLogRecord.builder()
                .paymentTransactionId(UUID.randomUUID())
                .amount(1)
                .type(FinancialOperationType.REFUND)
                .orderId(id)
                .timestamp(LocalDateTime.now())
                .userId(user.getId())
                .build();
        list.add(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(repository.findAllByUserIdAndOrderId(user.getId(), id)).thenReturn(list);
    }

    @Test
    public void getFinlogTest() throws UserNotFoundException {
        Assert.assertEquals(list.get(0).getPaymentTransactionId(), paymentService.getFinlog("username", id).get(0).getPaymentTransactionId());
        Assert.assertEquals(list.get(0).getOrderId(), paymentService.getFinlog("username", id).get(0).getOrderId());
        Assert.assertEquals(list.get(0).getAmount(), paymentService.getFinlog("username", id).get(0).getAmount());
        Assert.assertEquals(list.get(0).getTimestamp(), paymentService.getFinlog("username", id).get(0).getTimestamp());
    }

    @Test
    public void executeOrderPaymentTest() {
        Assert.assertEquals(PaymentSubmissionDto.class, paymentService.executeOrderPayment(userDetails, id).getClass());
    }
}
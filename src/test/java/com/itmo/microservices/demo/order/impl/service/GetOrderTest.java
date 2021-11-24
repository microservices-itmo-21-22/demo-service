package com.itmo.microservices.demo.order.impl.service;

import com.google.common.eventbus.EventBus;
import com.itmo.microservices.demo.items.impl.service.DefaultWarehouseService;
import com.itmo.microservices.demo.lib.common.order.dto.OrderStatusEnum;
import com.itmo.microservices.demo.lib.common.order.entity.OrderEntity;
import com.itmo.microservices.demo.lib.common.order.entity.OrderItemEntity;
import com.itmo.microservices.demo.lib.common.order.repository.OrderItemRepository;
import com.itmo.microservices.demo.lib.common.order.repository.OrderRepository;
import com.itmo.microservices.demo.order.api.service.OrderService;
import com.itmo.microservices.demo.payment.impl.model.PaymentLogRecordEntity;
import com.itmo.microservices.demo.users.impl.entity.AppUser;
import com.itmo.microservices.demo.users.impl.repository.UserRepository;
import com.itmo.microservices.demo.users.impl.service.DefaultUserService;
import com.itmo.microservices.demo.users.impl.service.JwtTokenManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("UnstableApiUsage")
public class GetOrderTest {
    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    OrderService orderService;
    OrderEntity orderEntity;

    @BeforeEach
    public void setUp() {
        List<OrderItemEntity> items = new ArrayList<>();
        List<PaymentLogRecordEntity> payments = new ArrayList<>();

        orderEntity = new OrderEntity(
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDateTime.now(),
                OrderStatusEnum.PAID,
                items,
                10,
                payments
        );

        orderRepository = mock(OrderRepository.class);
        when(orderRepository.findById(any()))
                .thenReturn(Optional.ofNullable(orderEntity));

        orderItemRepository = mock(OrderItemRepository.class);

        var appUser = new AppUser(
                "name",
                "password"
        );
        appUser.setId(UUID.randomUUID());

        var userRepository = mock(UserRepository.class);
        when(userRepository.findByUsername("name")).thenReturn(appUser);

        var passwordEncoder = mock(PasswordEncoder.class);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        var eventBus = mock(EventBus.class);
        var tokenManager = mock(JwtTokenManager.class);
        when(tokenManager.generateToken(any())).thenReturn("token");
        when(tokenManager.generateRefreshToken(any())).thenReturn("refreshToken");

        var userService = new DefaultUserService(userRepository, passwordEncoder, eventBus, tokenManager);

        orderService = new DefaultOrderService(orderRepository, orderItemRepository, mock(DefaultWarehouseService.class), userService);
    }

    @Test
    public void GetOrder() {
        assertEquals(10, orderService.getOrder(orderEntity.getId()).getDeliveryDuration());
    }
}
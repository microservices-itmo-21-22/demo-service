package com.itmo.microservices.demo.order.impl.service;

import com.itmo.microservices.demo.items.impl.service.DefaultWarehouseService;
import com.itmo.microservices.demo.order.api.model.OrderStatus;
import com.itmo.microservices.demo.order.api.service.OrderService;
import com.itmo.microservices.demo.order.impl.entities.OrderEntity;
import com.itmo.microservices.demo.order.impl.entities.OrderItemEntity;
import com.itmo.microservices.demo.order.impl.repository.OrderItemRepository;
import com.itmo.microservices.demo.payment.impl.model.UserAccountFinancialLogRecord;
import com.itmo.microservices.demo.tasks.impl.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
        List<UserAccountFinancialLogRecord> payments = new ArrayList<>();

        orderEntity = new OrderEntity(
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDateTime.now(),
                OrderStatus.PAID,
                items,
                10,
                payments
        );

        orderRepository = mock(OrderRepository.class);
        when(orderRepository.findById(any()))
                .thenReturn(Optional.ofNullable(orderEntity));

        orderItemRepository = mock(OrderItemRepository.class);

        orderService = new DefaultOrderService(orderRepository, orderItemRepository, mock(DefaultWarehouseService.class));
    }

    @Test
    public void GetOrder() {
        assertEquals(10, orderService.getOrder(orderEntity.getId()).getDeliveryDuration());
    }
}
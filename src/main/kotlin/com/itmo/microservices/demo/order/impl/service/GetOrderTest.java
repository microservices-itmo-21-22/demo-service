package com.itmo.microservices.demo.order.impl.service;

import com.itmo.microservices.demo.items.api.model.OrderItem;
import com.itmo.microservices.demo.order.api.model.OrderStatus;
import com.itmo.microservices.demo.order.api.service.OrderService;
import com.itmo.microservices.demo.order.impl.entity.OrderEntity;
import com.itmo.microservices.demo.order.impl.repository.OrderRepository;
import com.itmo.microservices.demo.payment.api.model.PaymentLogRecordDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("UnstableApiUsage")
public class GetOrderTest {
    OrderRepository orderRepository;
    OrderService orderService;
    OrderEntity orderEntity;

    @BeforeEach
    public void setUp() {
        Map<OrderItem, Integer> items = null;
        List<PaymentLogRecordDto> payments = null;

        orderEntity = new OrderEntity(
                UUID.randomUUID(),
                UUID.randomUUID(),
                0,
                OrderStatus.PAID,
                items,
                10,
                payments
        );

        orderRepository = mock(OrderRepository.class);
        when(orderRepository.findByIdOrNull(anyInt()))
                .thenReturn(orderEntity);

        orderService = new DefaultOrderService(orderRepository);
    }

    @Test
    public void GetOrder() {
        assertEquals(10, orderService.getOrder(orderEntity.getId()).getDeliveryDuration());
    }
}
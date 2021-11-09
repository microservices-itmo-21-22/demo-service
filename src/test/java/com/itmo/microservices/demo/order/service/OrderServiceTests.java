package com.itmo.microservices.demo.order.service;

import com.itmo.microservices.demo.order.api.model.OrderDto;
import com.itmo.microservices.demo.order.api.model.OrderStatus;
import com.itmo.microservices.demo.order.api.service.OrderService;
import com.itmo.microservices.demo.order.impl.entities.OrderEntity;
import com.itmo.microservices.demo.order.impl.repository.OrderItemRepository;
import com.itmo.microservices.demo.order.impl.service.DefaultOrderService;
import com.itmo.microservices.demo.tasks.impl.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderServiceTests {

	OrderRepository orderRepository;

	OrderItemRepository orderItemRepository;

	OrderService orderService;

	OrderEntity orderEntity;

	@BeforeEach
	public void init() {
		orderEntity = new OrderEntity(
				UUID.randomUUID(),
				UUID.randomUUID(),
				LocalDateTime.now(),
				OrderStatus.COLLECTING,
				new ArrayList<>(),
				30,
				new ArrayList<>()
		);

		orderRepository = mock(OrderRepository.class);
		when(orderRepository.findById(any()))
				.thenReturn(Optional.ofNullable(orderEntity));

		orderItemRepository = mock(OrderItemRepository.class);

		orderService = new DefaultOrderService(orderRepository, orderItemRepository);
	}

	@Test
	public void testGetOrder() {
		OrderDto result = orderService.getOrder(Objects.requireNonNull(orderEntity.getId()));
		assertEquals(OrderStatus.COLLECTING, result.getStatus());
	}
}

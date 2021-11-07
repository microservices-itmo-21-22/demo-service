package com.itmo.microservices.demo.order.service;

import com.itmo.microservices.demo.order.api.model.OrderDto;
import com.itmo.microservices.demo.order.api.service.OrderService;
import com.itmo.microservices.demo.order.impl.repository.OrderItemRepository;
import com.itmo.microservices.demo.tasks.impl.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTests {
	@Mock
	OrderRepository orderRepository;

	@Mock
	OrderItemRepository orderItemRepository;

	@InjectMocks
	OrderService orderService;

	@Test
	public void testGetOrder() {
		UUID uuid = new UUID(0L, 0L);
		assertThat(orderService.getOrder(uuid)).isEqualTo(null);
	}

	@Test
	public void testCreateOrder() {
		UUID uuid = new UUID(0L, 0L);
		OrderDto orderDto = orderService.createOrder(new User("username", "password", new ArrayList<>()));
		assertThat(orderDto).isNotEqualTo(null);
	}
}

package com.itmo.microservices.demo.order.service;

import com.google.common.eventbus.EventBus;
import com.itmo.microservices.demo.items.impl.service.DefaultWarehouseService;
import com.itmo.microservices.demo.lib.common.items.entity.CatalogItemEntity;
import com.itmo.microservices.demo.lib.common.items.repository.ItemRepository;
import com.itmo.microservices.demo.lib.common.order.dto.OrderStatusEnum;
import com.itmo.microservices.demo.lib.common.order.entity.OrderEntity;
import com.itmo.microservices.demo.lib.common.order.entity.OrderItemEntity;
import com.itmo.microservices.demo.lib.common.order.repository.OrderItemRepository;
import com.itmo.microservices.demo.lib.common.order.repository.OrderRepository;
import com.itmo.microservices.demo.order.impl.service.DefaultOrderService;
import com.itmo.microservices.demo.users.impl.entity.AppUser;
import com.itmo.microservices.demo.users.impl.repository.UserRepository;
import com.itmo.microservices.demo.users.impl.service.DefaultUserService;
import com.itmo.microservices.demo.users.impl.service.JwtTokenManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SuppressWarnings("UnstableApiUsage")
public class AddItemToTheCartTest {
    UUID catalogItemEntityUUID;
    UUID orderEntityUUID;

    OrderItemRepository orderItemRepository;
    DefaultOrderService orderService;

    @BeforeEach
    public void setUp() {
        catalogItemEntityUUID = UUID.randomUUID();
        var catalogItemEntity = new CatalogItemEntity(
                catalogItemEntityUUID,
                "test1",
                "this is a test",
                "10",
                5
        );

        var itemRepository = mock(ItemRepository.class);
        when(itemRepository.findById(catalogItemEntityUUID)).thenReturn(Optional.of(catalogItemEntity));

        var orderRepository = mock(OrderRepository.class);
        orderEntityUUID = UUID.randomUUID();
        var orderEntity = new OrderEntity(orderEntityUUID, UUID.randomUUID(), LocalDateTime.now(), OrderStatusEnum.COLLECTING, null, 5, null);
        when(orderRepository.getById(orderEntityUUID)).thenReturn(orderEntity);

        var itemService = new DefaultWarehouseService(itemRepository, mock(EventBus.class));

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

        orderItemRepository = mock(OrderItemRepository.class);
        orderService = new DefaultOrderService(orderRepository, orderItemRepository, itemService, userService);
    }

    @Test
    public void AddToCart() {
        ArgumentCaptor<OrderItemEntity> orderItemCaptor = ArgumentCaptor.forClass(OrderItemEntity.class);
        orderService.addItemToBasket(catalogItemEntityUUID,  orderEntityUUID,2);
        verify(orderItemRepository, times(1)).save(orderItemCaptor.capture());

        var receivedOrderItemEntity = orderItemCaptor.getValue();

        assertEquals(2, receivedOrderItemEntity.getAmount());
        assertEquals("test1", receivedOrderItemEntity.getTitle());
        assertEquals("10", receivedOrderItemEntity.getPrice());
    }
}

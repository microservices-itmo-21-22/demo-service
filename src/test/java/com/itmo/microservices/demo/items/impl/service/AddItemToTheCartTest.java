package com.itmo.microservices.demo.items.impl.service;

import com.google.common.eventbus.EventBus;
import com.itmo.microservices.demo.items.api.service.ItemService;
import com.itmo.microservices.demo.items.impl.entity.CatalogItemEntity;
import com.itmo.microservices.demo.items.impl.repository.ItemRepository;
import com.itmo.microservices.demo.order.api.model.OrderStatus;
import com.itmo.microservices.demo.order.impl.entities.OrderEntity;
import com.itmo.microservices.demo.order.impl.entities.OrderItemEntity;
import com.itmo.microservices.demo.order.impl.repository.OrderItemRepository;
import com.itmo.microservices.demo.order.impl.service.DefaultOrderService;
import com.itmo.microservices.demo.tasks.impl.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SuppressWarnings("UnstableApiUsage")
public class AddItemToTheCartTest {
    ItemRepository itemRepository;
    ItemService itemService;

    CatalogItemEntity catalogItemEntity;
    UUID catalogItemEntityUUID;

    OrderEntity orderEntity;
    UUID orderEntityUUID;
    OrderItemRepository orderItemRepository;

    @BeforeEach
    public void setUp() {
        catalogItemEntityUUID = UUID.randomUUID();
        catalogItemEntity = new CatalogItemEntity(
                catalogItemEntityUUID,
                "test1",
                "this is a test",
                "10",
                5
        );

        itemRepository = mock(ItemRepository.class);
        when(itemRepository.findById(catalogItemEntityUUID)).thenReturn(Optional.of(catalogItemEntity));

        var orderRepository = mock(OrderRepository.class);
        orderEntityUUID = UUID.randomUUID();
        orderEntity = new OrderEntity(orderEntityUUID, UUID.randomUUID(), LocalDateTime.now(), OrderStatus.COLLECTING, null, 5, null);
        when(orderRepository.getById(orderEntityUUID)).thenReturn(orderEntity);


        orderItemRepository = mock(OrderItemRepository.class);
        var defaultOrderService = new DefaultOrderService(orderRepository, orderItemRepository);


        itemService = new DefaultItemService(itemRepository,  defaultOrderService, mock(EventBus.class));
    }

    @Test
    public void AddToCart() {
        ArgumentCaptor<OrderItemEntity> orderItemCaptor = ArgumentCaptor.forClass(OrderItemEntity.class);
        itemService.addItemToBasket(catalogItemEntityUUID,  orderEntityUUID,2);
        verify(orderItemRepository, times(1)).save(orderItemCaptor.capture());

        var receivedOrderItemEntity = orderItemCaptor.getValue();

        assertEquals(2, receivedOrderItemEntity.getAmount());
        assertEquals("test1", receivedOrderItemEntity.getTitle());
        assertEquals("10", receivedOrderItemEntity.getPrice());
    }
}

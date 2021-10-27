package com.itmo.microservices.demo.items.impl.service;

import com.google.common.eventbus.EventBus;
import com.itmo.microservices.demo.common.exception.NotFoundException;
import com.itmo.microservices.demo.items.api.model.CatalogItem;
import com.itmo.microservices.demo.items.impl.entity.CatalogItemEntity;
import com.itmo.microservices.demo.items.impl.repository.ItemRepository;
import com.itmo.microservices.demo.items.api.service.ItemService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("UnstableApiUsage")
public class GetAllCatalogItemsTest {
    ItemRepository itemRepository;
    ItemService itemService;
    CatalogItemEntity catalogItemEntity1;
    CatalogItemEntity catalogItemEntity2;

    @BeforeEach
    public void setUp() {
        catalogItemEntity1 = new CatalogItemEntity(
                UUID.randomUUID(),
                "test1",
                "this is a test",
                10,
                1
        );
        catalogItemEntity2 = new CatalogItemEntity(
                UUID.randomUUID(),
                "test2",
                "this is a test",
                11,
                5
        );

        itemRepository = mock(ItemRepository.class);
        when(itemRepository.findAll())
                .thenReturn(List.of(catalogItemEntity1, catalogItemEntity2));

        var eventBus = mock(EventBus.class);

        itemService = new DefaultItemService(itemRepository,  eventBus);
    }

    @Test
    public void GetItems() {
        assertEquals(2, itemService.getCatalogItems().stream().count());
    }
}
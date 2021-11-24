package com.itmo.microservices.demo.items.impl.service;

import com.google.common.eventbus.EventBus;
import com.itmo.microservices.demo.items.api.service.WarehouseService;
import com.itmo.microservices.demo.lib.common.items.entity.CatalogItemEntity;
import com.itmo.microservices.demo.lib.common.items.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("UnstableApiUsage")
public class GetAllCatalogItemsTest {
    ItemRepository itemRepository;
    WarehouseService itemService;
    CatalogItemEntity catalogItemEntity1;
    CatalogItemEntity catalogItemEntity2;

    @BeforeEach
    public void setUp() {
        catalogItemEntity1 = new CatalogItemEntity(
                UUID.randomUUID(),
                "test1",
                "this is a test",
                "10",
                1
        );
        catalogItemEntity2 = new CatalogItemEntity(
                UUID.randomUUID(),
                "test2",
                "this is a test",
                "11",
                5
        );

        itemRepository = mock(ItemRepository.class);
        when(itemRepository.findAll())
                .thenReturn(List.of(catalogItemEntity1, catalogItemEntity2));

        var eventBus = mock(EventBus.class);

        itemService = new DefaultWarehouseService(itemRepository,  eventBus);
    }

    @Test
    public void GetItems() {
        assertEquals(2, itemService.getCatalogItems().stream().count());
    }
}
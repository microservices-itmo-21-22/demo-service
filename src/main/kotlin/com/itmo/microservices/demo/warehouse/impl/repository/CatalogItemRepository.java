package com.itmo.microservices.demo.warehouse.impl.repository;

import com.itmo.microservices.demo.warehouse.impl.entity.CatalogItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CatalogItemRepository extends JpaRepository<CatalogItem, UUID> {
    List<CatalogItem> findAll();
    boolean existsById(UUID id);
    CatalogItem findCatalogItemById(UUID id);
}

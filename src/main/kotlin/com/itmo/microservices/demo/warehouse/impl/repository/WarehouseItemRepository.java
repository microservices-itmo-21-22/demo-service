package com.itmo.microservices.demo.warehouse.impl.repository;

import com.itmo.microservices.demo.warehouse.impl.entity.WarehouseItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WarehouseItemRepository extends JpaRepository<WarehouseItem, UUID> {
    @Override
    boolean existsById(UUID uuid);
    WarehouseItem findWarehouseItemById(UUID uuid);
}

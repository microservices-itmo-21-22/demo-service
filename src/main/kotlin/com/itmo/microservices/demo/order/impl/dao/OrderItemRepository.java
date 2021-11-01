package com.itmo.microservices.demo.order.impl.dao;

import com.itmo.microservices.demo.order.impl.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
}

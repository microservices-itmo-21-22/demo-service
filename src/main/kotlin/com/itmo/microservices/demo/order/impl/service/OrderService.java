package com.itmo.microservices.demo.order.impl.service;

import com.itmo.microservices.demo.order.api.dto.Booking;
import com.itmo.microservices.demo.order.api.dto.CatalogItem;
import com.itmo.microservices.demo.order.api.dto.Order;
import com.itmo.microservices.demo.order.api.dto.OrderItem;
import com.itmo.microservices.demo.order.impl.dao.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService implements IOrderService{
    private final OrderRepository repository;

    @Autowired
    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Order createOrder(Order order) {
        return new Order();
    }

    @Override
    public Order getOrderById(UUID uuid) {
        com.itmo.microservices.demo.order.impl.entity.Order orderEntity = repository.getById(uuid);

        // entity.CatalogItem -> dto.CatalogItem
        List<CatalogItem> dtoItems = orderEntity.getCatalogItems().stream()
                .map(item -> new CatalogItem(item., item.getTitle(), item.getDescription(),
                        item.getPrice(), item.getAmount())).collect(Collectors.toList());

        // List<dto.CatalogItem> -> Map<OrderItem, Integer>
        Map<OrderItem, Integer> itemList = dtoItems.stream()
                .collect(Collectors.toMap(item -> new OrderItem(item.getUuid(), item.getTitle(), item.getPrice()),
                        CatalogItem::getAmount));

        return new Order(orderEntity.getUuid(), orderEntity.getTimeCreated(), itemList, orderEntity.getStatus());
    }

    @Override
    public void updateOrder(UUID orderId, UUID itemId, int amount) {

    }

    @Override
    public Booking book(UUID orderId) {
        return null;
    }

    @Override
    public void selectDeliveryTime(UUID orderId, int seconds) {

    }
}

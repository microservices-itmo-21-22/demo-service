package com.itmo.microservices.demo.order.impl.service;

import com.itmo.microservices.demo.order.api.dto.Booking;
import com.itmo.microservices.demo.order.api.dto.CatalogItem;
import com.itmo.microservices.demo.order.api.dto.Order;
import com.itmo.microservices.demo.order.api.dto.OrderItem;
import com.itmo.microservices.demo.order.impl.dao.CatalogItemRepository;
import com.itmo.microservices.demo.order.impl.dao.OrderItemRepository;
import com.itmo.microservices.demo.order.impl.dao.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService implements IOrderService{
    private final OrderRepository repository;
    private final CatalogItemRepository catalogItemRepository;
    private final OrderItemRepository orderItemRepository;

    private static final String API_URL = "http://http://77.234.215.138:30019/api/";

    @Autowired
    public OrderService(OrderRepository repository, CatalogItemRepository catalogItemRepository, OrderItemRepository orderItemRepository) {
        this.repository = repository;
        this.catalogItemRepository = catalogItemRepository;
        this.orderItemRepository = orderItemRepository;
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
                .map(item -> new CatalogItem(item.getUuid(), item.getTitle(), item.getDescription(),
                        item.getPrice(), item.getAmount())).collect(Collectors.toList());

        // List<dto.CatalogItem> -> Map<OrderItem, Integer>
        Map<OrderItem, Integer> itemList = dtoItems.stream()
                .collect(Collectors.toMap(item -> new OrderItem(item.getUuid(), item.getTitle(), item.getPrice()),
                        CatalogItem::getAmount));

        return new Order(orderEntity.getUuid(), orderEntity.getTimeCreated(), itemList, orderEntity.getStatus(), orderEntity.getDeliveryInfo());
    }

    @Override
    public void putItemToOrder(UUID orderId, UUID itemId, int amount) {
        Order order = getOrderById(orderId);
        com.itmo.microservices.demo.order.impl.entity.OrderItem item = orderItemRepository.getById(itemId);
        OrderItem itemDto = new OrderItem(item.getUuid(), item.getTitle(), item.getPrice());

        order.getItemList().put(itemDto, amount);
    }

    @Override
    public Booking book(UUID orderId) throws IOException {
        Order order = getOrderById(orderId);

        URL url = new URL(API_URL + "warehouse/book");
        sendRequest(order, url);

        return null;
    }

    @Override
    public void selectDeliveryTime(UUID orderId, int seconds) throws IOException {
        Order order = getOrderById(orderId);
        order.setDeliveryInfo(new Timestamp(seconds));
        URL url = new URL(API_URL + "delivery/doDelivery");
        sendRequest(order, url);
    }

    private void sendRequest(Order order, URL url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("order", order.toString());

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
        out.flush();
        out.close();
    }
}

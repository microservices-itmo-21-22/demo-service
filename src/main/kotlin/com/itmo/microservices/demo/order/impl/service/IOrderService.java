package com.itmo.microservices.demo.order.impl.service;

import com.itmo.microservices.demo.order.api.dto.Booking;
import com.itmo.microservices.demo.order.api.dto.Order;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.UUID;

public interface IOrderService {
    Order createOrder(Order order);
    Order getOrderById(UUID orderId);
    void putItemToOrder(UUID orderId, UUID itemId, int amount);
    Booking book(UUID orderId) throws IOException;
    void selectDeliveryTime(UUID orderId, int seconds) throws IOException;
}

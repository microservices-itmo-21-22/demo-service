package com.itmo.microservices.demo.delivery.impl.service;

import com.itmo.microservices.demo.delivery.api.service.DeliveryService;
import com.itmo.microservices.demo.lib.common.delivery.dto.BookingDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDeliveryService {

	DeliveryService deliveryService = new DefaultDeliveryService();

	@Test
	public void testGetAvailableDeliverySlots() {
		List<LocalDateTime> availableDeliverySlots = deliveryService.getAvailableDeliverySlots(1);
		assertTrue(availableDeliverySlots.isEmpty());
	}

	@Test
	public void testSetDesiredDeliveryTime() {
		UUID uuid = UUID.randomUUID();
		BookingDto bookingDto = deliveryService.setDesiredDeliveryTime(uuid, 1);
		assertTrue(bookingDto.getFailedItems().isEmpty());
	}
}

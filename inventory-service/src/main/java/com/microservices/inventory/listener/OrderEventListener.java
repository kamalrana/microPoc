package com.microservices.inventory.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.microservices.inventory.service.InventoryService;

@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);
    private final InventoryService inventoryService;

    public OrderEventListener(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(
            topics = "order-placed-topic",
            groupId = "inventory-group"
    )
    public void handleOrderEvent(String orderEventPayload) {
        log.info("Inventory Service intercepted an event from 'order-placed-topic'");
        try {
            // Your internal business logic to decrement inventory allocation
            // inventoryService.deductStock(orderEventPayload);
            log.info("Inventory stock allocation processed successfully.");
        } catch (Exception e) {
            log.error("Failed to update stock metrics for incoming order event", e);
        }
    }
}

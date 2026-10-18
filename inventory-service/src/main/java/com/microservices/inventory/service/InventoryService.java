package com.microservices.inventory.service;


import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservices.inventory.dto.InventoryResponse;
import com.microservices.inventory.entity.InventoryItem;
import com.microservices.inventory.exception.InventoryNotFoundException;
import com.microservices.inventory.repository.InventoryRepository;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Checks stock levels for a collection of bulk SKU codes passed by the API Gateway or Order Service.
     */
    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCodes) {
        log.info("Checking stock levels for SKU list: {}", skuCodes);
        
        return inventoryRepository.findBySkuCodeIn(skuCodes).stream()
                .map(item -> new InventoryResponse(
                        item.getSkuCode(),
                        item.getQuantity() > 0
                ))
                .collect(Collectors.toList());
    }

    /**
     * Checks stock status for an explicit single SKU code endpoint query.
     */
    @Transactional(readOnly = true)
    public InventoryResponse checkStock(String skuCode) {
        log.info("Checking stock level for a single SKU: {}", skuCode);
        
        InventoryItem item = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory item not found for SKU: " + skuCode));
        
        return new InventoryResponse(item.getSkuCode(), item.getQuantity() > 0);
    }

    /**
     * Handles asynchronous stock updates triggered by native Kafka events.
     */
    @Transactional
    public void deductStock(String orderEventPayload) {
        log.info("Processing asynchronous inventory reduction for event payload: {}", orderEventPayload);
        
        // Use an ObjectMapper instance (Jackson) to deserialize your JSON event payload string here:
        /* 
        try {
            OrderPlacedEvent event = objectMapper.readValue(orderEventPayload, OrderPlacedEvent.class);
            for (OrderItem item : event.getOrderLineItemsDtoList()) {
                InventoryItem inventory = inventoryRepository.findBySkuCode(item.getSkuCode())
                    .orElseThrow(() -> new InventoryNotFoundException("SKU not found: " + item.getSkuCode()));
                
                inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
                inventoryRepository.save(inventory);
            }
            log.info("Stock updated successfully.");
        } catch (Exception e) {
            log.error("Failed to parse and update stock for payload", e);
        }
        */
    }
}

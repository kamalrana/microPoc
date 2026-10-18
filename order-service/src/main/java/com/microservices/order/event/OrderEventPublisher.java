package com.microservices.order.event;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    // Topic name must exactly match the consumers' subscription topic name
    private static final String TOPIC_NAME = "order-placed-topic";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes an order placement event payload asynchronously over to the Kafka Cluster.
     * @param orderPlacedEvent The structured event domain payload
     */
    public void publishOrderPlacedEvent(Object orderPlacedEvent) {
        log.info("Preparing to publish order event to Kafka topic: '{}'", TOPIC_NAME);

        try {
            this.kafkaTemplate.send(TOPIC_NAME, orderPlacedEvent)
                    .whenComplete((result, exception) -> {
                        if (exception == null) {
                            log.info("Successfully sent order event offset metadata: [{}]",
                                    result.getRecordMetadata().toString());
                        } else {
                            log.error("Failed to append order event record log downstream", exception);
                        }
                    });
        } catch (Exception e) {
            log.error("Critical execution breakdown within the Kafka publisher template layer", e);
        }
    }

    public void publishOrderPlaced(Long id, String productId, Integer quantity, BigDecimal price) {
        log.info("Preparing to publish order placed event for order ID: {}", id);
        
        try {
            // Using a generic Map or an inline payload object for the event
            java.util.Map<String, Object> event = new java.util.HashMap<>();
            event.put("id", id);
            event.put("productId", productId);
            event.put("quantity", quantity);
            event.put("price", price);
            
            publishOrderPlacedEvent(event);
        } catch (Exception e) {
            log.error("Failed to construct and publish order placed event for order ID: {}", id, e);
            throw new RuntimeException("Failed to publish order placed event", e);
        }
    }
}

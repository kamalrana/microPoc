package com.microservices.payment.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.microservices.payment.service.PaymentService;

@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);
    private final PaymentService paymentService;

    public OrderEventListener(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(
            topics = "order-placed-topic",
            groupId = "payment-group"
    )
    public void handleOrderEvent(String orderEventPayload) {
        log.info("Payment Service intercepted an event from 'order-placed-topic'");
        try {
            // Your internal business logic to trigger payment billing profiles
            // paymentService.processTransaction(orderEventPayload);
            log.info("Payment processing verification step finalized.");
        } catch (Exception e) {
            log.error("Failed to execute transactional profile for incoming order event", e);
        }
    }
}

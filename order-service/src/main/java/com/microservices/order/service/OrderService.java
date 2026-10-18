package com.microservices.order.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservices.order.entity.Order;
import com.microservices.order.event.OrderEventPublisher;
import com.microservices.order.repository.OrderRepository;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository, OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Order createOrder(Order order) {
        order.setStatus("PENDING");
        Order savedOrder = orderRepository.save(order);
        eventPublisher.publishOrderPlaced(savedOrder.getId(), savedOrder.getProductId(), savedOrder.getQuantity(), savedOrder.getPrice());
        return savedOrder;
    }
}

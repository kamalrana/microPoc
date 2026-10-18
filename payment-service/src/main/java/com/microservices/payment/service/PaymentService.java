package com.microservices.payment.service;

import com.microservices.payment.dto.PaymentRequest;
import com.microservices.payment.dto.PaymentResponse;
import com.microservices.payment.entity.Payment;
import com.microservices.payment.entity.PaymentStatus;
import com.microservices.payment.exception.PaymentNotFoundException;
import com.microservices.payment.exception.PaymentProcessingException;
import com.microservices.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * Synchronous Rest endpoint action to process an explicit user checkout request.
     */
    @Transactional
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        log.info("Processing synchronous payment authorization for Order reference ID: {}", paymentRequest.getOrderId());
        
        try {
            Payment payment = new Payment();
            payment.setOrderId(paymentRequest.getOrderId());
            payment.setAmount(paymentRequest.getAmount());
            payment.setTransactionId(UUID.randomUUID().toString());
            
            // Simple mock business validation rule
            if (paymentRequest.getAmount() <= 0) {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                throw new PaymentProcessingException("Transaction rejected: Invalid payment amount specified.");
            }
            
            payment.setStatus(PaymentStatus.SUCCESSFUL);
            Payment savedPayment = paymentRepository.save(payment);
            
            return new PaymentResponse(
                    savedPayment.getTransactionId(),
                    savedPayment.getOrderId(),
                    savedPayment.getStatus().toString()
            );
        } catch (PaymentProcessingException ex) {
            throw ex;
        } catch (Exception e) {
            log.error("Internal crash when compiling database payment structures", e);
            throw new PaymentProcessingException("Failed to register authorization due to an internal system error.");
        }
    }

    /**
     * Retrieves status information for an explicit single checkout profile lookup.
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(String orderId) {
        log.info("Fetching transaction status record logs for Order ID: {}", orderId);
        
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("No payment records located targeting Order ID: " + orderId));
                
        return new PaymentResponse(
                payment.getTransactionId(),
                payment.getOrderId(),
                payment.getStatus().toString()
        );
    }

    /**
     * Asynchronous background hook triggered automatically by incoming Kafka event consumer listeners.
     */
    @Transactional
    public void processTransaction(String orderEventPayload) {
        log.info("Executing asynchronous payment transaction ledger allocation from incoming order stream event: {}", orderEventPayload);
        
        // This acts as the processing sink hook invoked by your custom OrderEventListener.
        // Use Jackson ObjectMapper to parse fields from orderEventPayload and execute transaction calculations.
    }
}

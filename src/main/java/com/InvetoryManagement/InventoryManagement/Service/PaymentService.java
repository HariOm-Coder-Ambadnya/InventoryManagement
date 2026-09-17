package com.InvetoryManagement.InventoryManagement.Service;

import com.InvetoryManagement.InventoryManagement.DTO.CreatePaymentRequest;
import com.InvetoryManagement.InventoryManagement.DTO.PaymentResponse;
import com.InvetoryManagement.InventoryManagement.DTO.ProcessPaymentRequest;
import com.InvetoryManagement.InventoryManagement.Entity.*;
import com.InvetoryManagement.InventoryManagement.Exception.BadRequestException;
import com.InvetoryManagement.InventoryManagement.Exception.ForbiddenException;
import com.InvetoryManagement.InventoryManagement.Exception.ResourceNotFoundException;
import com.InvetoryManagement.InventoryManagement.Repository.OrderRepository;
import com.InvetoryManagement.InventoryManagement.Repository.PaymentRepository;
import com.InvetoryManagement.InventoryManagement.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          OrderRepository orderRepository,
                          UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getId());
        response.setOrderId(payment.getOrder().getId());
        response.setAmount(payment.getAmount());
        response.setPaymentMethod(payment.getPaymentMethod().name());
        response.setPaymentStatus(payment.getPaymentStatus().name());
        response.setTransactionId(payment.getTransactionId());
        response.setCreatedAt(payment.getCreated_at());
        return response;
    }

    @Transactional
    public PaymentResponse createPayment(String email, CreatePaymentRequest request) {
        User user = getUserByEmail(email);

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.getOrderId()));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have access to this order");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot create payment for a cancelled order");
        }

        if (paymentRepository.existsByOrderId(order.getId())) {
            throw new BadRequestException("Payment already exists for this order");
        }

        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid payment method: " + request.getPaymentMethod());
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .paymentMethod(paymentMethod)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        return mapToPaymentResponse(savedPayment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(String email, String paymentId) {
        User user = getUserByEmail(email);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (!payment.getOrder().getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have access to this payment");
        }

        return mapToPaymentResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentForOrder(String email, String orderId) {
        User user = getUserByEmail(email);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have access to this order");
        }

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + orderId));

        return mapToPaymentResponse(payment);
    }

    @Transactional
    public PaymentResponse processPayment(String email, String paymentId, ProcessPaymentRequest request) {
        User user = getUserByEmail(email);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (!payment.getOrder().getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have access to this payment");
        }

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new BadRequestException("Payment can only be processed from PENDING status. Current: " + payment.getPaymentStatus());
        }

        payment.setPaymentStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(payment);

        boolean success = "SUCCESS".equalsIgnoreCase(request.getResult());

        if (success) {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        } else {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        Payment updatedPayment = paymentRepository.save(payment);
        return mapToPaymentResponse(updatedPayment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByIdAdmin(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));
        return mapToPaymentResponse(payment);
    }
}

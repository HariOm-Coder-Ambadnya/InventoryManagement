package com.InvetoryManagement.InventoryManagement.Controller;

import com.InvetoryManagement.InventoryManagement.DTO.CreatePaymentRequest;
import com.InvetoryManagement.InventoryManagement.DTO.PaymentResponse;
import com.InvetoryManagement.InventoryManagement.DTO.ProcessPaymentRequest;
import com.InvetoryManagement.InventoryManagement.Service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            Authentication authentication,
            @RequestBody CreatePaymentRequest request) {
        String email = authentication.getName();
        return ResponseEntity.ok(paymentService.createPayment(email, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            Authentication authentication,
            @PathVariable String id) {
        String email = authentication.getName();
        return ResponseEntity.ok(paymentService.getPaymentById(email, id));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentForOrder(
            Authentication authentication,
            @PathVariable String orderId) {
        String email = authentication.getName();
        return ResponseEntity.ok(paymentService.getPaymentForOrder(email, orderId));
    }

    @PostMapping("/{paymentId}/process")
    public ResponseEntity<PaymentResponse> processPayment(
            Authentication authentication,
            @PathVariable String paymentId,
            @RequestBody ProcessPaymentRequest request) {
        String email = authentication.getName();
        return ResponseEntity.ok(paymentService.processPayment(email, paymentId, request));
    }
}

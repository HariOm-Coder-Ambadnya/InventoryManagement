package com.InvetoryManagement.InventoryManagement.Controller;

import com.InvetoryManagement.InventoryManagement.DTO.CreateShippingRequest;
import com.InvetoryManagement.InventoryManagement.DTO.ShippingResponse;
import com.InvetoryManagement.InventoryManagement.Service.ShippingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipping")
public class ShippingController {

    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @PostMapping
    public ResponseEntity<ShippingResponse> createShipping(
            Authentication authentication,
            @RequestBody CreateShippingRequest request) {
        String email = authentication.getName();
        return ResponseEntity.ok(shippingService.createShipping(email, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShippingResponse> getShippingById(
            Authentication authentication,
            @PathVariable String id) {
        String email = authentication.getName();
        return ResponseEntity.ok(shippingService.getShippingById(email, id));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ShippingResponse> getShippingForOrder(
            Authentication authentication,
            @PathVariable String orderId) {
        String email = authentication.getName();
        return ResponseEntity.ok(shippingService.getShippingForOrder(email, orderId));
    }
}

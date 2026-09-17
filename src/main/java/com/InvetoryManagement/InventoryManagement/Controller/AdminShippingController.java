package com.InvetoryManagement.InventoryManagement.Controller;

import com.InvetoryManagement.InventoryManagement.DTO.ShippingResponse;
import com.InvetoryManagement.InventoryManagement.DTO.UpdateShippingStatusRequest;
import com.InvetoryManagement.InventoryManagement.Service.ShippingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/shipping")
public class AdminShippingController {

    private final ShippingService shippingService;

    public AdminShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @GetMapping
    public ResponseEntity<List<ShippingResponse>> getAllShipping() {
        return ResponseEntity.ok(shippingService.getAllShipping());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShippingResponse> getShippingById(@PathVariable String id) {
        return ResponseEntity.ok(shippingService.getShippingByIdAdmin(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ShippingResponse> updateShippingStatus(
            @PathVariable String id,
            @RequestBody UpdateShippingStatusRequest request) {
        return ResponseEntity.ok(shippingService.updateShippingStatus(id, request.getStatus()));
    }
}

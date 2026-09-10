package com.InvetoryManagement.InventoryManagement.Controller;

import com.InvetoryManagement.InventoryManagement.DTO.StockRequest;
import com.InvetoryManagement.InventoryManagement.Entity.StockHistory;
import com.InvetoryManagement.InventoryManagement.Service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/{productId}/stock")
    public ResponseEntity<StockHistory> updateStock(
            @PathVariable String productId,
            @RequestBody StockRequest request) {

        StockHistory history =
                inventoryService.updateStock(productId, request);

        if (history == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(history);
    }

    @GetMapping("/{productId}/history")
    public ResponseEntity<List<StockHistory>> getProductStockHistory(
            @PathVariable String productId) {

        return ResponseEntity.ok(
                inventoryService.getProductStockHistory(productId)
        );
    }

    @GetMapping("/history")
    public ResponseEntity<List<StockHistory>> getAllStockHistory() {

        return ResponseEntity.ok(
                inventoryService.getAllStockHistory()
        );
    }
}
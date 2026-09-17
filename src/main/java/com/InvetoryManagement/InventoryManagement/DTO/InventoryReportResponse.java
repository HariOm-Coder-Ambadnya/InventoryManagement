package com.InvetoryManagement.InventoryManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReportResponse {

    private long totalProducts;
    private long activeProducts;
    private long inactiveProducts;
    private long totalStockUnits;
    private long lowStockCount;
    private long outOfStockCount;
    private List<LowStockItem> lowStockItems;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LowStockItem {
        private String productId;
        private String productName;
        private int quantity;
        private int minStock;
        private String category;
    }
}

package com.InvetoryManagement.InventoryManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesReportResponse {

    private long totalOrders;
    private long totalItemsSold;
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<ProductSalesSummary> productSales;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductSalesSummary {
        private String productId;
        private String productName;
        private long quantitySold;
        private BigDecimal revenue;
    }
}

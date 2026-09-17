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
public class OrderReportResponse {

    private long totalOrders;
    private long pending;
    private long confirmed;
    private long processing;
    private long shipped;
    private long delivered;
    private long cancelled;
    private List<OrderSummary> orders;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderSummary {
        private String orderId;
        private String customerName;
        private BigDecimal totalAmount;
        private String status;
        private String paymentStatus;
        private String shippingStatus;
        private LocalDateTime createdAt;
    }
}

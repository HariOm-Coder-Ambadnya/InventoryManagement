package com.InvetoryManagement.InventoryManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingReportResponse {

    private long totalShipments;
    private long pending;
    private long packed;
    private long shipped;
    private long outForDelivery;
    private long delivered;
    private long cancelled;
    private List<ShipmentSummary> shipments;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShipmentSummary {
        private String shippingId;
        private String orderId;
        private String customerName;
        private String status;
        private String trackingNumber;
        private LocalDate estimatedDeliveryDate;
    }
}

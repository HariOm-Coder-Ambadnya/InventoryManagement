package com.InvetoryManagement.InventoryManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {

    private UserStats users;
    private ProductStats products;
    private InventoryStats inventory;
    private CategoryStats categories;
    private OrderStats orders;
    private PaymentStats payments;
    private ShippingStats shipping;
    private SalesStats sales;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStats {
        private long totalUsers;
        private long activeUsers;
        private long inactiveUsers;
        private long totalCustomers;
        private long totalAdmins;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductStats {
        private long totalProducts;
        private long activeProducts;
        private long inactiveProducts;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryStats {
        private long totalStockUnits;
        private long lowStockProducts;
        private long outOfStockProducts;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryStats {
        private long totalCategories;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderStats {
        private long totalOrders;
        private long pendingOrders;
        private long confirmedOrders;
        private long processingOrders;
        private long shippedOrders;
        private long deliveredOrders;
        private long cancelledOrders;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentStats {
        private long pendingPayments;
        private long successfulPayments;
        private long failedPayments;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShippingStats {
        private long pendingShipments;
        private long packedShipments;
        private long shippedShipments;
        private long outForDeliveryShipments;
        private long deliveredShipments;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesStats {
        private BigDecimal totalRevenue;
        private long totalItemsSold;
        private BigDecimal averageOrderValue;
    }
}

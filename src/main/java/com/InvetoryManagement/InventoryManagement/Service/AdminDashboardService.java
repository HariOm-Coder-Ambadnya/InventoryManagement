package com.InvetoryManagement.InventoryManagement.Service;

import com.InvetoryManagement.InventoryManagement.DTO.*;
import com.InvetoryManagement.InventoryManagement.Entity.*;
import com.InvetoryManagement.InventoryManagement.Exception.BadRequestException;
import com.InvetoryManagement.InventoryManagement.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final ShippingRepository shippingRepository;

    private static final List<OrderStatus> QUALIFYING_ORDER_STATUSES = List.of(
            OrderStatus.CONFIRMED, OrderStatus.PROCESSING, OrderStatus.SHIPPED, OrderStatus.DELIVERED
    );

    public AdminDashboardService(UserRepository userRepository,
                                  ProductRepository productRepository,
                                  CategoryRepository categoryRepository,
                                  OrderRepository orderRepository,
                                  OrderItemRepository orderItemRepository,
                                  PaymentRepository paymentRepository,
                                  ShippingRepository shippingRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.shippingRepository = shippingRepository;
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboard() {
        AdminDashboardResponse response = new AdminDashboardResponse();

        // Users
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByActive(true);
        long inactiveUsers = userRepository.countByActive(false);
        long totalCustomers = userRepository.countByRole(Role.CUSTOMER);
        long totalAdmins = userRepository.countByRole(Role.ADMIN);
        response.setUsers(new AdminDashboardResponse.UserStats(totalUsers, activeUsers, inactiveUsers, totalCustomers, totalAdmins));

        // Products
        long totalProducts = productRepository.count();
        long activeProducts = productRepository.countByActive(true);
        long inactiveProducts = productRepository.countByActive(false);
        response.setProducts(new AdminDashboardResponse.ProductStats(totalProducts, activeProducts, inactiveProducts));

        // Inventory
        long totalStockUnits = productRepository.sumQuantity();
        long lowStockProducts = productRepository.findLowStockProducts().size();
        long outOfStockProducts = productRepository.findOutOfStockProducts().size();
        response.setInventory(new AdminDashboardResponse.InventoryStats(totalStockUnits, lowStockProducts, outOfStockProducts));

        // Categories
        long totalCategories = categoryRepository.count();
        response.setCategories(new AdminDashboardResponse.CategoryStats(totalCategories));

        // Orders
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        long confirmedOrders = orderRepository.countByStatus(OrderStatus.CONFIRMED);
        long processingOrders = orderRepository.countByStatus(OrderStatus.PROCESSING);
        long shippedOrders = orderRepository.countByStatus(OrderStatus.SHIPPED);
        long deliveredOrders = orderRepository.countByStatus(OrderStatus.DELIVERED);
        long cancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED);
        response.setOrders(new AdminDashboardResponse.OrderStats(totalOrders, pendingOrders, confirmedOrders, processingOrders, shippedOrders, deliveredOrders, cancelledOrders));

        // Payments
        long pendingPayments = paymentRepository.countByPaymentStatus(PaymentStatus.PENDING);
        long successfulPayments = paymentRepository.countByPaymentStatus(PaymentStatus.SUCCESS);
        long failedPayments = paymentRepository.countByPaymentStatus(PaymentStatus.FAILED);
        response.setPayments(new AdminDashboardResponse.PaymentStats(pendingPayments, successfulPayments, failedPayments));

        // Shipping
        long pendingShipments = shippingRepository.countByShippingStatus(ShippingStatus.PENDING);
        long packedShipments = shippingRepository.countByShippingStatus(ShippingStatus.PACKED);
        long shippedShipments = shippingRepository.countByShippingStatus(ShippingStatus.SHIPPED);
        long outForDeliveryShipments = shippingRepository.countByShippingStatus(ShippingStatus.OUT_FOR_DELIVERY);
        long deliveredShipments = shippingRepository.countByShippingStatus(ShippingStatus.DELIVERED);
        response.setShipping(new AdminDashboardResponse.ShippingStats(pendingShipments, packedShipments, shippedShipments, outForDeliveryShipments, deliveredShipments));

        // Sales
        BigDecimal totalRevenue = orderRepository.sumTotalAmountByStatuses(QUALIFYING_ORDER_STATUSES);
        long totalItemsSold = orderRepository.sumQuantitySoldByStatuses(QUALIFYING_ORDER_STATUSES);
        BigDecimal averageOrderValue = BigDecimal.ZERO;
        long qualifyingOrderCount = orderRepository.countByStatuses(QUALIFYING_ORDER_STATUSES);
        if (qualifyingOrderCount > 0) {
            averageOrderValue = totalRevenue.divide(BigDecimal.valueOf(qualifyingOrderCount), 2, RoundingMode.HALF_UP);
        }
        response.setSales(new AdminDashboardResponse.SalesStats(totalRevenue, totalItemsSold, averageOrderValue));

        return response;
    }

    @Transactional(readOnly = true)
    public InventoryReportResponse getInventoryReport() {
        long totalProducts = productRepository.count();
        long activeProducts = productRepository.countByActive(true);
        long inactiveProducts = productRepository.countByActive(false);
        long totalStockUnits = productRepository.sumQuantity();
        List<Product> lowStockItems = productRepository.findLowStockProducts();
        List<Product> outOfStockItems = productRepository.findOutOfStockProducts();

        List<InventoryReportResponse.LowStockItem> lowStockDTOs = new ArrayList<>();
        for (Product p : lowStockItems) {
            lowStockDTOs.add(new InventoryReportResponse.LowStockItem(
                    p.getId(), p.getName(), p.getQuantity(), p.getMinstock(), p.getCategory()));
        }

        return new InventoryReportResponse(
                totalProducts, activeProducts, inactiveProducts, totalStockUnits,
                lowStockItems.size(), outOfStockItems.size(), lowStockDTOs);
    }

    @Transactional(readOnly = true)
    public SalesReportResponse getSalesReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<OrderStatus> statuses = QUALIFYING_ORDER_STATUSES;

        BigDecimal totalRevenue;
        long totalItemsSold;
        long totalOrders;
        List<Object[]> productSalesData;

        if (startDate != null && endDate != null) {
            totalRevenue = orderRepository.sumTotalAmountByStatusesAndDateRange(statuses, startDate, endDate);
            totalItemsSold = orderRepository.sumQuantitySoldByStatusesAndDateRange(statuses, startDate, endDate);
            totalOrders = orderRepository.countByStatusesAndDateRange(statuses, startDate, endDate);
            productSalesData = orderItemRepository.aggregateSalesByProductAndDateRange(statuses, startDate, endDate);
        } else {
            totalRevenue = orderRepository.sumTotalAmountByStatuses(statuses);
            totalItemsSold = orderRepository.sumQuantitySoldByStatuses(statuses);
            totalOrders = orderRepository.countByStatuses(statuses);
            productSalesData = orderItemRepository.aggregateSalesByProduct(statuses);
        }

        BigDecimal averageOrderValue = BigDecimal.ZERO;
        if (totalOrders > 0) {
            averageOrderValue = totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP);
        }

        List<SalesReportResponse.ProductSalesSummary> productSales = new ArrayList<>();
        for (Object[] row : productSalesData) {
            productSales.add(new SalesReportResponse.ProductSalesSummary(
                    (String) row[0], (String) row[1], ((Number) row[2]).longValue(), (BigDecimal) row[3]));
        }

        return new SalesReportResponse(totalOrders, totalItemsSold, totalRevenue, averageOrderValue, startDate, endDate, productSales);
    }

    @Transactional(readOnly = true)
    public List<SalesTrendResponse> getSalesTrend() {
        List<Order> qualifyingOrders = orderRepository.findAll().stream()
                .filter(o -> QUALIFYING_ORDER_STATUSES.contains(o.getStatus()))
                .collect(Collectors.toList());

        Map<LocalDate, List<Order>> ordersByDate = qualifyingOrders.stream()
                .collect(Collectors.groupingBy(o -> o.getCreated_at().toLocalDate()));

        List<SalesTrendResponse> trend = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Order>> entry : ordersByDate.entrySet()) {
            BigDecimal dayRevenue = entry.getValue().stream()
                    .map(Order::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            trend.add(new SalesTrendResponse(entry.getKey(), entry.getValue().size(), dayRevenue));
        }

        trend.sort((a, b) -> a.getDate().compareTo(b.getDate()));
        return trend;
    }

    @Transactional(readOnly = true)
    public OrderReportResponse getOrderReport() {
        long total = orderRepository.count();
        long pending = orderRepository.countByStatus(OrderStatus.PENDING);
        long confirmed = orderRepository.countByStatus(OrderStatus.CONFIRMED);
        long processing = orderRepository.countByStatus(OrderStatus.PROCESSING);
        long shipped = orderRepository.countByStatus(OrderStatus.SHIPPED);
        long delivered = orderRepository.countByStatus(OrderStatus.DELIVERED);
        long cancelled = orderRepository.countByStatus(OrderStatus.CANCELLED);

        List<Order> allOrders = orderRepository.findAllOrderByCreated_atDesc();
        List<OrderReportResponse.OrderSummary> orderSummaries = new ArrayList<>();
        for (Order order : allOrders) {
            Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
            Shipping shipping = shippingRepository.findByOrderId(order.getId()).orElse(null);

            orderSummaries.add(new OrderReportResponse.OrderSummary(
                    order.getId(),
                    order.getUser().getName(),
                    order.getTotalAmount(),
                    order.getStatus().name(),
                    payment != null ? payment.getPaymentStatus().name() : "NO_PAYMENT",
                    shipping != null ? shipping.getShippingStatus().name() : "NO_SHIPPING",
                    order.getCreated_at()));
        }

        return new OrderReportResponse(total, pending, confirmed, processing, shipped, delivered, cancelled, orderSummaries);
    }

    @Transactional(readOnly = true)
    public List<ProductReportResponse> getProductReport() {
        List<Object[]> productData = orderItemRepository.aggregateProductPerformance(QUALIFYING_ORDER_STATUSES);

        Map<String, Object[]> productMap = new java.util.LinkedHashMap<>();
        for (Object[] row : productData) {
            productMap.put((String) row[0], row);
        }

        List<ProductReportResponse> report = new ArrayList<>();
        for (Map.Entry<String, Object[]> entry : productMap.entrySet()) {
            Object[] row = entry.getValue();
            report.add(new ProductReportResponse(
                    (String) row[0],
                    (String) row[1],
                    (String) row[2],
                    ((Number) row[3]).intValue(),
                    ((Number) row[4]).longValue(),
                    (BigDecimal) row[5],
                    (Boolean) row[6]));
        }

        List<Product> allProducts = productRepository.findAll();
        for (Product p : allProducts) {
            boolean found = report.stream().anyMatch(r -> r.getProductId().equals(p.getId()));
            if (!found) {
                report.add(new ProductReportResponse(
                        p.getId(), p.getName(), p.getCategory(), p.getQuantity(),
                        0, BigDecimal.ZERO, p.isActive()));
            }
        }

        return report;
    }

    @Transactional(readOnly = true)
    public List<LowStockResponse> getLowStockReport() {
        List<Product> lowStock = productRepository.findLowStockProducts();
        List<LowStockResponse> report = new ArrayList<>();
        for (Product p : lowStock) {
            report.add(new LowStockResponse(p.getId(), p.getName(), p.getQuantity(), p.getMinstock(), p.getCategory()));
        }
        return report;
    }

    @Transactional(readOnly = true)
    public List<OutOfStockResponse> getOutOfStockReport() {
        List<Product> outOfStock = productRepository.findOutOfStockProducts();
        List<OutOfStockResponse> report = new ArrayList<>();
        for (Product p : outOfStock) {
            report.add(new OutOfStockResponse(p.getId(), p.getName(), p.getQuantity(), p.getCategory()));
        }
        return report;
    }

    @Transactional(readOnly = true)
    public PaymentReportResponse getPaymentReport() {
        long total = paymentRepository.count();
        long successful = paymentRepository.countByPaymentStatus(PaymentStatus.SUCCESS);
        long pending = paymentRepository.countByPaymentStatus(PaymentStatus.PENDING);
        long failed = paymentRepository.countByPaymentStatus(PaymentStatus.FAILED);
        long cancelled = paymentRepository.countByPaymentStatus(PaymentStatus.CANCELLED);
        long refunded = paymentRepository.countByPaymentStatus(PaymentStatus.REFUNDED);

        BigDecimal totalSuccessfulAmount = paymentRepository.sumAmountByPaymentStatus(PaymentStatus.SUCCESS);
        BigDecimal cardAmount = paymentRepository.sumAmountByPaymentStatusAndMethod(PaymentStatus.SUCCESS, PaymentMethod.CARD);
        BigDecimal upiAmount = paymentRepository.sumAmountByPaymentStatusAndMethod(PaymentStatus.SUCCESS, PaymentMethod.UPI);
        BigDecimal netBankingAmount = paymentRepository.sumAmountByPaymentStatusAndMethod(PaymentStatus.SUCCESS, PaymentMethod.NET_BANKING);
        BigDecimal codAmount = paymentRepository.sumAmountByPaymentStatusAndMethod(PaymentStatus.SUCCESS, PaymentMethod.CASH_ON_DELIVERY);

        return new PaymentReportResponse(total, successful, pending, failed, cancelled, refunded,
                totalSuccessfulAmount, cardAmount, upiAmount, netBankingAmount, codAmount);
    }

    @Transactional(readOnly = true)
    public ShippingReportResponse getShippingReport() {
        long total = shippingRepository.count();
        long pending = shippingRepository.countByShippingStatus(ShippingStatus.PENDING);
        long packed = shippingRepository.countByShippingStatus(ShippingStatus.PACKED);
        long shipped = shippingRepository.countByShippingStatus(ShippingStatus.SHIPPED);
        long outForDelivery = shippingRepository.countByShippingStatus(ShippingStatus.OUT_FOR_DELIVERY);
        long delivered = shippingRepository.countByShippingStatus(ShippingStatus.DELIVERED);
        long cancelled = shippingRepository.countByShippingStatus(ShippingStatus.CANCELLED);

        List<Shipping> allShipping = shippingRepository.findAllOrderByCreatedAtDesc();
        List<ShippingReportResponse.ShipmentSummary> summaries = new ArrayList<>();
        for (Shipping s : allShipping) {
            summaries.add(new ShippingReportResponse.ShipmentSummary(
                    s.getId(),
                    s.getOrder().getId(),
                    s.getOrder().getUser().getName(),
                    s.getShippingStatus().name(),
                    s.getTrackingNumber(),
                    s.getEstimatedDeliveryDate()));
        }

        return new ShippingReportResponse(total, pending, packed, shipped, outForDelivery, delivered, cancelled, summaries);
    }

    @Transactional(readOnly = true)
    public UserReportResponse getUserReport() {
        long total = userRepository.count();
        long active = userRepository.countByActive(true);
        long inactive = userRepository.countByActive(false);
        long customers = userRepository.countByRole(Role.CUSTOMER);
        long admins = userRepository.countByRole(Role.ADMIN);
        return new UserReportResponse(total, active, inactive, customers, admins);
    }

    @Transactional(readOnly = true)
    public List<TopProductResponse> getTopProducts(int limit) {
        if (limit <= 0) {
            limit = 10;
        }
        List<Object[]> salesData = orderItemRepository.aggregateSalesByProduct(QUALIFYING_ORDER_STATUSES);
        List<TopProductResponse> topProducts = new ArrayList<>();
        for (Object[] row : salesData) {
            if (topProducts.size() >= limit) break;
            topProducts.add(new TopProductResponse(
                    (String) row[0], (String) row[1], ((Number) row[2]).longValue(), (BigDecimal) row[3]));
        }
        return topProducts;
    }

    @Transactional(readOnly = true)
    public List<CategorySalesResponse> getCategorySalesReport() {
        List<Object[]> categoryData = orderItemRepository.aggregateSalesByCategory(QUALIFYING_ORDER_STATUSES);
        List<CategorySalesResponse> report = new ArrayList<>();
        for (Object[] row : categoryData) {
            report.add(new CategorySalesResponse(
                    (String) row[0], ((Number) row[1]).longValue(), (BigDecimal) row[2]));
        }
        return report;
    }
}

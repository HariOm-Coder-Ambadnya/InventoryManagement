package com.InvetoryManagement.InventoryManagement.Controller;

import com.InvetoryManagement.InventoryManagement.DTO.*;
import com.InvetoryManagement.InventoryManagement.Exception.BadRequestException;
import com.InvetoryManagement.InventoryManagement.Service.AdminDashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboard() {
        return ResponseEntity.ok(adminDashboardService.getDashboard());
    }

    @GetMapping("/reports/inventory")
    public ResponseEntity<InventoryReportResponse> getInventoryReport() {
        return ResponseEntity.ok(adminDashboardService.getInventoryReport());
    }

    @GetMapping("/reports/sales")
    public ResponseEntity<SalesReportResponse> getSalesReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        if (start != null && end != null && start.isAfter(end)) {
            throw new BadRequestException("startDate must be before or equal to endDate");
        }
        return ResponseEntity.ok(adminDashboardService.getSalesReport(start, end));
    }

    @GetMapping("/reports/sales/trend")
    public ResponseEntity<List<SalesTrendResponse>> getSalesTrend() {
        return ResponseEntity.ok(adminDashboardService.getSalesTrend());
    }

    @GetMapping("/reports/orders")
    public ResponseEntity<OrderReportResponse> getOrderReport() {
        return ResponseEntity.ok(adminDashboardService.getOrderReport());
    }

    @GetMapping("/reports/products")
    public ResponseEntity<List<ProductReportResponse>> getProductReport() {
        return ResponseEntity.ok(adminDashboardService.getProductReport());
    }

    @GetMapping("/reports/low-stock")
    public ResponseEntity<List<LowStockResponse>> getLowStockReport() {
        return ResponseEntity.ok(adminDashboardService.getLowStockReport());
    }

    @GetMapping("/reports/out-of-stock")
    public ResponseEntity<List<OutOfStockResponse>> getOutOfStockReport() {
        return ResponseEntity.ok(adminDashboardService.getOutOfStockReport());
    }

    @GetMapping("/reports/payments")
    public ResponseEntity<PaymentReportResponse> getPaymentReport() {
        return ResponseEntity.ok(adminDashboardService.getPaymentReport());
    }

    @GetMapping("/reports/shipping")
    public ResponseEntity<ShippingReportResponse> getShippingReport() {
        return ResponseEntity.ok(adminDashboardService.getShippingReport());
    }

    @GetMapping("/reports/users")
    public ResponseEntity<UserReportResponse> getUserReport() {
        return ResponseEntity.ok(adminDashboardService.getUserReport());
    }

    @GetMapping("/reports/top-products")
    public ResponseEntity<List<TopProductResponse>> getTopProducts(
            @RequestParam(defaultValue = "10") int limit) {
        if (limit <= 0) {
            limit = 10;
        }
        return ResponseEntity.ok(adminDashboardService.getTopProducts(limit));
    }

    @GetMapping("/reports/categories")
    public ResponseEntity<List<CategorySalesResponse>> getCategorySalesReport() {
        return ResponseEntity.ok(adminDashboardService.getCategorySalesReport());
    }
}

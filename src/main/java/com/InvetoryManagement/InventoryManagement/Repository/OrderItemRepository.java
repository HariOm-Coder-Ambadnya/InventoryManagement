package com.InvetoryManagement.InventoryManagement.Repository;

import com.InvetoryManagement.InventoryManagement.Entity.OrderItem;
import com.InvetoryManagement.InventoryManagement.Entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, String> {

    @Query("SELECT oi.product.id, oi.product.name, COALESCE(SUM(oi.quantity), 0), COALESCE(SUM(oi.subtotal), 0) FROM OrderItem oi JOIN oi.order o WHERE o.status IN :statuses GROUP BY oi.product.id, oi.product.name ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> aggregateSalesByProduct(@Param("statuses") List<OrderStatus> statuses);

    @Query("SELECT oi.product.id, oi.product.name, COALESCE(SUM(oi.quantity), 0), COALESCE(SUM(oi.subtotal), 0) FROM OrderItem oi JOIN oi.order o WHERE o.status IN :statuses AND o.created_at BETWEEN :start AND :end GROUP BY oi.product.id, oi.product.name ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> aggregateSalesByProductAndDateRange(@Param("statuses") List<OrderStatus> statuses, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT oi.product.category, COALESCE(SUM(oi.quantity), 0), COALESCE(SUM(oi.subtotal), 0) FROM OrderItem oi JOIN oi.order o WHERE o.status IN :statuses GROUP BY oi.product.category ORDER BY SUM(oi.subtotal) DESC")
    List<Object[]> aggregateSalesByCategory(@Param("statuses") List<OrderStatus> statuses);

    @Query("SELECT oi.product.id, oi.product.name, oi.product.category, oi.product.quantity, COALESCE(SUM(oi.quantity), 0), COALESCE(SUM(oi.subtotal), 0), oi.product.active FROM OrderItem oi JOIN oi.order o WHERE o.status IN :statuses GROUP BY oi.product.id, oi.product.name, oi.product.category, oi.product.quantity, oi.product.active")
    List<Object[]> aggregateProductPerformance(@Param("statuses") List<OrderStatus> statuses);
}

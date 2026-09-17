package com.InvetoryManagement.InventoryManagement.Repository;

import com.InvetoryManagement.InventoryManagement.Entity.Shipping;
import com.InvetoryManagement.InventoryManagement.Entity.ShippingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ShippingRepository extends JpaRepository<Shipping, String> {

    Optional<Shipping> findByOrderId(String orderId);

    boolean existsByOrderId(String orderId);

    long countByShippingStatus(ShippingStatus status);

    @Query("SELECT s FROM Shipping s ORDER BY s.created_at DESC")
    List<Shipping> findAllOrderByCreatedAtDesc();
}

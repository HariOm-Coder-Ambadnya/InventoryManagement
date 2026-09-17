package com.InvetoryManagement.InventoryManagement.Repository;

import com.InvetoryManagement.InventoryManagement.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByCategory(String category);

    long countByActive(boolean active);

    @Query("SELECT COALESCE(SUM(p.quantity), 0) FROM Product p")
    long sumQuantity();

    @Query("SELECT p FROM Product p WHERE p.quantity <= p.minstock AND p.quantity > 0 ORDER BY p.quantity ASC")
    List<Product> findLowStockProducts();

    @Query("SELECT p FROM Product p WHERE p.quantity = 0")
    List<Product> findOutOfStockProducts();
}

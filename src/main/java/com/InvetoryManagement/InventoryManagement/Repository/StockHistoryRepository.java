package com.InvetoryManagement.InventoryManagement.Repository;

import com.InvetoryManagement.InventoryManagement.Entity.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockHistoryRepository extends JpaRepository<StockHistory, String> {
    List<StockHistory> findByProductId(String productId);
}

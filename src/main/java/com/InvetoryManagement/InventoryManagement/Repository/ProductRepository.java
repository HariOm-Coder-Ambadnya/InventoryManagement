package com.InvetoryManagement.InventoryManagement.Repository;

import com.InvetoryManagement.InventoryManagement.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByCategory(String category);

}

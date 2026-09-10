package com.InvetoryManagement.InventoryManagement.Repository;

import com.InvetoryManagement.InventoryManagement.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
}

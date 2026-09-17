package com.InvetoryManagement.InventoryManagement.Repository;

import com.InvetoryManagement.InventoryManagement.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, String> {

    Optional<Cart> findByUserId(String userId);
}

package com.InvetoryManagement.InventoryManagement.Repository;

import com.InvetoryManagement.InventoryManagement.Entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, String> {

    Optional<CartItem> findByCartIdAndProductId(String cartId, String productId);

    @Modifying
    void deleteByCartId(String cartId);
}

package com.InvetoryManagement.InventoryManagement.Controller;

import com.InvetoryManagement.InventoryManagement.DTO.*;
import com.InvetoryManagement.InventoryManagement.Service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getMyCart(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(cartService.getMyCart(email));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(
            Authentication authentication,
            @RequestBody AddToCartRequest request) {
        String email = authentication.getName();
        return ResponseEntity.ok(cartService.addToCart(email, request));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateCartItem(
            Authentication authentication,
            @PathVariable String itemId,
            @RequestBody UpdateCartItemRequest request) {
        String email = authentication.getName();
        return ResponseEntity.ok(cartService.updateCartItem(email, itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeCartItem(
            Authentication authentication,
            @PathVariable String itemId) {
        String email = authentication.getName();
        return ResponseEntity.ok(cartService.removeCartItem(email, itemId));
    }

    @DeleteMapping
    public ResponseEntity<CartResponse> clearCart(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(cartService.clearCart(email));
    }
}

package com.InvetoryManagement.InventoryManagement.Service;

import com.InvetoryManagement.InventoryManagement.DTO.*;
import com.InvetoryManagement.InventoryManagement.Entity.Cart;
import com.InvetoryManagement.InventoryManagement.Entity.CartItem;
import com.InvetoryManagement.InventoryManagement.Entity.Product;
import com.InvetoryManagement.InventoryManagement.Entity.User;
import com.InvetoryManagement.InventoryManagement.Exception.BadRequestException;
import com.InvetoryManagement.InventoryManagement.Exception.ResourceNotFoundException;
import com.InvetoryManagement.InventoryManagement.Repository.CartItemRepository;
import com.InvetoryManagement.InventoryManagement.Repository.CartRepository;
import com.InvetoryManagement.InventoryManagement.Repository.ProductRepository;
import com.InvetoryManagement.InventoryManagement.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Cart getOrCreateCart(User user) {
        Optional<Cart> existingCart = cartRepository.findByUserId(user.getId());
        if (existingCart.isPresent()) {
            return existingCart.get();
        }
        Cart cart = Cart.builder()
                .user(user)
                .items(new ArrayList<>())
                .build();
        return cartRepository.save(cart);
    }

    private CartItemResponse mapToCartItemResponse(CartItem item) {
        CartItemResponse response = new CartItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProduct().getId());
        response.setProductName(item.getProduct().getName());
        response.setPrice(item.getPrice());
        response.setQuantity(item.getQuantity());
        response.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        return response;
    }

    @Transactional(readOnly = true)
    public CartResponse getMyCart(String email) {
        User user = getUserByEmail(email);
        Cart cart = getOrCreateCart(user);

        List<CartItemResponse> itemResponses = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            CartItemResponse itemResponse = mapToCartItemResponse(item);
            itemResponses.add(itemResponse);
            total = total.add(itemResponse.getSubtotal());
        }

        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setItems(itemResponses);
        response.setTotal(total);
        return response;
    }

    @Transactional
    public CartResponse addToCart(String email, AddToCartRequest request) {
        if (request.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        User user = getUserByEmail(email);
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        if (!product.isActive()) {
            throw new BadRequestException("Product is not available");
        }

        if (product.getQuantity() <= 0) {
            throw new BadRequestException("Product is out of stock");
        }

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + request.getQuantity();

            if (newQuantity > product.getQuantity()) {
                throw new BadRequestException("Requested quantity exceeds available stock. Available: " + product.getQuantity());
            }

            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            if (request.getQuantity() > product.getQuantity()) {
                throw new BadRequestException("Requested quantity exceeds available stock. Available: " + product.getQuantity());
            }

            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .price(product.getPrice())
                    .build();
            cartItemRepository.save(cartItem);
        }

        return getMyCart(email);
    }

    @Transactional
    public CartResponse updateCartItem(String email, String itemId, UpdateCartItemRequest request) {
        if (request.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        User user = getUserByEmail(email);
        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cart item does not belong to your cart");
        }

        Product product = cartItem.getProduct();
        if (!product.isActive()) {
            throw new BadRequestException("Product is no longer available");
        }

        if (request.getQuantity() > product.getQuantity()) {
            throw new BadRequestException("Requested quantity exceeds available stock. Available: " + product.getQuantity());
        }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        return getMyCart(email);
    }

    @Transactional
    public CartResponse removeCartItem(String email, String itemId) {
        User user = getUserByEmail(email);
        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cart item does not belong to your cart");
        }

        cartItemRepository.delete(cartItem);

        return getMyCart(email);
    }

    @Transactional
    public CartResponse clearCart(String email) {
        User user = getUserByEmail(email);
        Cart cart = getOrCreateCart(user);

        cartItemRepository.deleteByCartId(cart.getId());

        return getMyCart(email);
    }
}

package com.InvetoryManagement.InventoryManagement.Service;

import com.InvetoryManagement.InventoryManagement.DTO.CreateShippingRequest;
import com.InvetoryManagement.InventoryManagement.DTO.ShippingResponse;
import com.InvetoryManagement.InventoryManagement.Entity.*;
import com.InvetoryManagement.InventoryManagement.Exception.BadRequestException;
import com.InvetoryManagement.InventoryManagement.Exception.ForbiddenException;
import com.InvetoryManagement.InventoryManagement.Exception.ResourceNotFoundException;
import com.InvetoryManagement.InventoryManagement.Repository.OrderRepository;
import com.InvetoryManagement.InventoryManagement.Repository.ShippingRepository;
import com.InvetoryManagement.InventoryManagement.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ShippingService {

    private final ShippingRepository shippingRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Value("${shipping.estimated-days:7}")
    private int estimatedDeliveryDays;

    public ShippingService(ShippingRepository shippingRepository,
                           OrderRepository orderRepository,
                           UserRepository userRepository) {
        this.shippingRepository = shippingRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private ShippingResponse mapToShippingResponse(Shipping shipping) {
        ShippingResponse response = new ShippingResponse();
        response.setShippingId(shipping.getId());
        response.setOrderId(shipping.getOrder().getId());
        response.setRecipientName(shipping.getRecipientName());
        response.setPhone(shipping.getPhone());
        response.setAddressLine1(shipping.getAddressLine1());
        response.setAddressLine2(shipping.getAddressLine2());
        response.setCity(shipping.getCity());
        response.setState(shipping.getState());
        response.setPostalCode(shipping.getPostalCode());
        response.setCountry(shipping.getCountry());
        response.setShippingStatus(shipping.getShippingStatus().name());
        response.setTrackingNumber(shipping.getTrackingNumber());
        response.setEstimatedDeliveryDate(shipping.getEstimatedDeliveryDate());
        response.setCreatedAt(shipping.getCreated_at());
        return response;
    }

    private String generateTrackingNumber() {
        return "TRK-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Transactional
    public ShippingResponse createShipping(String email, CreateShippingRequest request) {
        User user = getUserByEmail(email);

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.getOrderId()));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have access to this order");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot create shipping for a cancelled order");
        }

        if (shippingRepository.existsByOrderId(order.getId())) {
            throw new BadRequestException("Shipping already exists for this order");
        }

        if (request.getRecipientName() == null || request.getRecipientName().isBlank()) {
            throw new BadRequestException("Recipient name is required");
        }

        if (request.getPhone() == null || request.getPhone().isBlank()) {
            throw new BadRequestException("Phone number is required");
        }

        if (request.getAddressLine1() == null || request.getAddressLine1().isBlank()) {
            throw new BadRequestException("Address line 1 is required");
        }

        if (request.getCity() == null || request.getCity().isBlank()) {
            throw new BadRequestException("City is required");
        }

        if (request.getState() == null || request.getState().isBlank()) {
            throw new BadRequestException("State is required");
        }

        if (request.getPostalCode() == null || request.getPostalCode().isBlank()) {
            throw new BadRequestException("Postal code is required");
        }

        if (request.getCountry() == null || request.getCountry().isBlank()) {
            throw new BadRequestException("Country is required");
        }

        Shipping shipping = Shipping.builder()
                .order(order)
                .recipientName(request.getRecipientName())
                .phone(request.getPhone())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .shippingStatus(ShippingStatus.PENDING)
                .trackingNumber(generateTrackingNumber())
                .estimatedDeliveryDate(LocalDate.now().plusDays(estimatedDeliveryDays))
                .build();

        Shipping savedShipping = shippingRepository.save(shipping);
        return mapToShippingResponse(savedShipping);
    }

    @Transactional(readOnly = true)
    public ShippingResponse getShippingById(String email, String shippingId) {
        User user = getUserByEmail(email);

        Shipping shipping = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping not found with id: " + shippingId));

        if (!shipping.getOrder().getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have access to this shipping");
        }

        return mapToShippingResponse(shipping);
    }

    @Transactional(readOnly = true)
    public ShippingResponse getShippingForOrder(String email, String orderId) {
        User user = getUserByEmail(email);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have access to this order");
        }

        Shipping shipping = shippingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping not found for order: " + orderId));

        return mapToShippingResponse(shipping);
    }

    @Transactional(readOnly = true)
    public List<ShippingResponse> getAllShipping() {
        List<Shipping> shippingList = shippingRepository.findAll();
        return shippingList.stream()
                .map(this::mapToShippingResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ShippingResponse getShippingByIdAdmin(String shippingId) {
        Shipping shipping = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping not found with id: " + shippingId));
        return mapToShippingResponse(shipping);
    }

    @Transactional
    public ShippingResponse updateShippingStatus(String shippingId, String status) {
        Shipping shipping = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping not found with id: " + shippingId));

        ShippingStatus newStatus;
        try {
            newStatus = ShippingStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid shipping status: " + status);
        }

        if (shipping.getShippingStatus() == ShippingStatus.DELIVERED) {
            throw new BadRequestException("Cannot update status of a delivered shipment");
        }

        if (shipping.getShippingStatus() == ShippingStatus.CANCELLED) {
            throw new BadRequestException("Cannot update status of a cancelled shipment");
        }

        if (!isValidShippingTransition(shipping.getShippingStatus(), newStatus)) {
            throw new BadRequestException(
                    "Cannot transition from " + shipping.getShippingStatus() + " to " + newStatus
            );
        }

        shipping.setShippingStatus(newStatus);
        Shipping updatedShipping = shippingRepository.save(shipping);
        return mapToShippingResponse(updatedShipping);
    }

    private boolean isValidShippingTransition(ShippingStatus current, ShippingStatus next) {
        return switch (current) {
            case PENDING -> next == ShippingStatus.PACKED || next == ShippingStatus.CANCELLED;
            case PACKED -> next == ShippingStatus.SHIPPED || next == ShippingStatus.CANCELLED;
            case SHIPPED -> next == ShippingStatus.OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY -> next == ShippingStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }
}

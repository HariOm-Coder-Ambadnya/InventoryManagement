package com.InvetoryManagement.InventoryManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingResponse {

    private String shippingId;

    private String orderId;

    private String recipientName;

    private String phone;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String postalCode;

    private String country;

    private String shippingStatus;

    private String trackingNumber;

    private LocalDate estimatedDeliveryDate;

    private LocalDateTime createdAt;
}

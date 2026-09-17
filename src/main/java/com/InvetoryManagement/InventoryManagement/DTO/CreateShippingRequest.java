package com.InvetoryManagement.InventoryManagement.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateShippingRequest {

    private String orderId;

    private String recipientName;

    private String phone;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String postalCode;

    private String country;
}

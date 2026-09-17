package com.InvetoryManagement.InventoryManagement.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentRequest {

    private String orderId;

    private String paymentMethod;
}

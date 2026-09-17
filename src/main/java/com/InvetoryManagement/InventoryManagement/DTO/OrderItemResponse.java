package com.InvetoryManagement.InventoryManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private String id;

    private String productId;

    private String productName;

    private BigDecimal price;

    private int quantity;

    private BigDecimal subtotal;
}

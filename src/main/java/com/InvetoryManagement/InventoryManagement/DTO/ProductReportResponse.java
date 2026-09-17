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
public class ProductReportResponse {

    private String productId;
    private String productName;
    private String category;
    private int currentStock;
    private long quantitySold;
    private BigDecimal revenue;
    private boolean active;
}

package com.InvetoryManagement.InventoryManagement.DTO;

import com.InvetoryManagement.InventoryManagement.Entity.StockType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockRequest {

    private int quantity;

    private StockType type;

    private String reason;

}

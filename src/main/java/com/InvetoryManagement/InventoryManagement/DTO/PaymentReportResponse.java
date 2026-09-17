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
public class PaymentReportResponse {

    private long totalPayments;
    private long successfulPayments;
    private long pendingPayments;
    private long failedPayments;
    private long cancelledPayments;
    private long refundedPayments;
    private BigDecimal totalSuccessfulAmount;
    private BigDecimal cardAmount;
    private BigDecimal upiAmount;
    private BigDecimal netBankingAmount;
    private BigDecimal codAmount;
}

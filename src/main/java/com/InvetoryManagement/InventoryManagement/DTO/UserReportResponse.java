package com.InvetoryManagement.InventoryManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReportResponse {

    private long totalUsers;
    private long activeUsers;
    private long inactiveUsers;
    private long totalCustomers;
    private long totalAdmins;
}

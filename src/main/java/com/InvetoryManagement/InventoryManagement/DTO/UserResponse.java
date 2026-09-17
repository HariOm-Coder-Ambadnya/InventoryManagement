package com.InvetoryManagement.InventoryManagement.DTO;

import com.InvetoryManagement.InventoryManagement.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private Role role;
    private boolean active;
}

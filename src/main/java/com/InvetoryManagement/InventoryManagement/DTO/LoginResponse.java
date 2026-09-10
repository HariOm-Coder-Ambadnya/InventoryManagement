package com.InvetoryManagement.InventoryManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String message;
    private String token;
    private String email;
    private String role;
}
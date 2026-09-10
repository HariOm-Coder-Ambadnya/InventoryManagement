package com.InvetoryManagement.InventoryManagement.Controller;

import com.InvetoryManagement.InventoryManagement.DTO.LoginRequest;
import com.InvetoryManagement.InventoryManagement.DTO.LoginResponse;
import com.InvetoryManagement.InventoryManagement.DTO.RegisterRequest;
import com.InvetoryManagement.InventoryManagement.Entity.User;
import com.InvetoryManagement.InventoryManagement.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        User user = authService.register(request);

        return ResponseEntity.ok(
                new com.InvetoryManagement.InventoryManagement.DTO.RegisterResponse(
                        "User registered successfully",
                        user.getEmail()
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        String token = authService.login(request);

        User user = authService.getUserByEmail(request.getEmail());

        return ResponseEntity.ok(
                new LoginResponse(
                        "Login successful",
                        token,
                        user.getEmail(),
                        user.getRole().name()
                )
        );
    }
}
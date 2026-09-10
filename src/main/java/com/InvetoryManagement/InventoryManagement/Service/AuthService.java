package com.InvetoryManagement.InventoryManagement.Service;

import com.InvetoryManagement.InventoryManagement.DTO.LoginRequest;
import com.InvetoryManagement.InventoryManagement.DTO.RegisterRequest;
import com.InvetoryManagement.InventoryManagement.Entity.Role;
import com.InvetoryManagement.InventoryManagement.Entity.User;
import com.InvetoryManagement.InventoryManagement.Repository.UserRepository;
import com.InvetoryManagement.InventoryManagement.Security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .active(true)
                .build();

        return userRepository.save(user);
    }

    public String login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        if (!user.isActive()) {
            throw new RuntimeException("User account is inactive");
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid email or password");
        }

        return jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );
    }

    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }
}
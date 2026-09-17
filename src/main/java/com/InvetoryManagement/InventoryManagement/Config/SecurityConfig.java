package com.InvetoryManagement.InventoryManagement.Config;

import com.InvetoryManagement.InventoryManagement.Security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no auth needed
                        .requestMatchers("/api/auth/**").permitAll()

                        // Product read endpoints - any authenticated user (ADMIN or CUSTOMER)
                        .requestMatchers(HttpMethod.GET, "/api/products/**").authenticated()

                        // Product write endpoints - ADMIN only
                        .requestMatchers(HttpMethod.POST, "/api/products").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

                        // Category read - any authenticated user
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").authenticated()

                        // Category write - ADMIN only
                        .requestMatchers(HttpMethod.POST, "/api/categories").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN")

                        // Inventory - ADMIN only for stock updates, any user can view history
                        .requestMatchers(HttpMethod.GET, "/api/inventory/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/inventory/**").hasRole("ADMIN")

                        // User profile - any authenticated user
                        .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/me").authenticated()

                        // Admin user management - ADMIN only
                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/{id}/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasRole("ADMIN")

                        // Cart - CUSTOMER only
                        .requestMatchers(HttpMethod.GET, "/api/cart").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.POST, "/api/cart/**").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.PUT, "/api/cart/**").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.DELETE, "/api/cart/**").hasRole("CUSTOMER")

                        // Orders - CUSTOMER only
                        .requestMatchers(HttpMethod.POST, "/api/orders").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/orders").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/orders/{id}").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.PATCH, "/api/orders/{id}/cancel").hasRole("CUSTOMER")

                        // Admin order management - ADMIN only
                        .requestMatchers(HttpMethod.GET, "/api/admin/orders").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/admin/orders/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/admin/orders/{id}/status").hasRole("ADMIN")

                        // Payments - CUSTOMER only
                        .requestMatchers(HttpMethod.POST, "/api/payments").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/payments/{id}").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/payments/order/{orderId}").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.POST, "/api/payments/{paymentId}/process").hasRole("CUSTOMER")

                        // Admin payment management - ADMIN only
                        .requestMatchers(HttpMethod.GET, "/api/admin/payments").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/admin/payments/{id}").hasRole("ADMIN")

                        // Shipping - CUSTOMER only
                        .requestMatchers(HttpMethod.POST, "/api/shipping").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/shipping/{id}").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/shipping/order/{orderId}").hasRole("CUSTOMER")

                        // Admin shipping management - ADMIN only
                        .requestMatchers(HttpMethod.GET, "/api/admin/shipping").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/admin/shipping/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/admin/shipping/{id}/status").hasRole("ADMIN")

                        // Admin dashboard and reports - ADMIN only
                        .requestMatchers(HttpMethod.GET, "/api/admin/dashboard").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/admin/reports/**").hasRole("ADMIN")

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

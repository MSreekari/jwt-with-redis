package com.project.JwtWithRedis.controller;

import com.project.JwtWithRedis.dto.AuthResponse;
import com.project.JwtWithRedis.dto.LoginRequest;
import com.project.JwtWithRedis.dto.RegisterRequest;
import com.project.JwtWithRedis.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint for creating a brand-new user account inside PostgreSQL.
     * POST http://localhost:8080/api/v1/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        String response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint for user authentication. Returns an access and refresh token pair
     * and caches the refresh token validation payload directly into Redis.
     * POST http://localhost:8080/api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
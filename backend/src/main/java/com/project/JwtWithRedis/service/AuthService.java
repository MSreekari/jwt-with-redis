package com.project.JwtWithRedis.service;

import com.project.JwtWithRedis.dto.AuthResponse;
import com.project.JwtWithRedis.dto.LoginRequest;
import com.project.JwtWithRedis.dto.RegisterRequest;
import com.project.JwtWithRedis.entity.Role;
import com.project.JwtWithRedis.entity.User;
import com.project.JwtWithRedis.repository.UserRepository;
import com.project.JwtWithRedis.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTokenService redisTokenService;

    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already taken!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);

        userRepository.save(user);
        return "User registered successfully!";
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Generate stateless and stateful token pairs
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getId());

        // Whitelist the refresh token inside Redis cache for 7 days
        redisTokenService.whitelistRefreshToken(user.getEmail(), refreshToken, jwtUtil.jwtRefreshExpirationMs);

        return new AuthResponse(accessToken, refreshToken);
    }
}
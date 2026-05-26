package com.project.JwtWithRedis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    /**
     * A protected endpoint that extracts user identity straight from the security context.
     * Route: GET http://localhost:8080/api/v1/dashboard/info
     * Header required: Authorization = Bearer <your_access_token>
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getDashboardInfo(@AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();

        response.put("status", "Success");
        response.put("message", "Welcome to the secure dashboard layer!");
        response.put("authenticatedUserEmail", userDetails.getUsername());
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    /**
     * An admin-only test endpoint to verify role management later.
     * Route: GET http://localhost:8080/api/v1/dashboard/admin-stats
     */
    @GetMapping("/admin-stats")
    public ResponseEntity<String> getAdminStats() {
        return ResponseEntity.ok("Sensors and system metrics: Core engine running at 100% capacity.");
    }
}
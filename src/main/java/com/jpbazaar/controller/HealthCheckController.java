package com.jpbazaar.controller;

import com.jpbazaar.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Health check controller to verify Spring Boot foundation setup.
 */
@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health", description = "Application System Health APIs")
public class HealthCheckController {

    @GetMapping
    @Operation(summary = "Check application status", description = "Returns system health status, service name, and version.")
    public ResponseEntity<ApiResponse<Map<String, String>>> healthCheck() {
        Map<String, String> statusInfo = Map.of(
                "status", "UP",
                "application", "JPBazaar",
                "version", "1.0.0"
        );
        return ResponseEntity.ok(ApiResponse.success("JPBazaar application is running smoothly", statusInfo));
    }
}

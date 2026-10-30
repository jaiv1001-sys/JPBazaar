package com.jpbazaar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Standardized API Response wrapper for all REST endpoints in JPBazaar.
 *
 * @param <T> Payload type
 */
@Schema(description = "Standardized unified API response envelope")
public record ApiResponse<T>(
        @Schema(description = "Indicates whether the request was processed successfully", example = "true")
        boolean success,

        @Schema(description = "Human-readable message describing the operation outcome", example = "Operation successful")
        String message,

        @Schema(description = "Response payload data")
        T data,

        @Schema(description = "Timestamp when the response was generated", example = "2026-10-28T21:30:00")
        LocalDateTime timestamp
) {
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(T data) {
        return success("Operation successful", data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data, LocalDateTime.now());
    }
}

package com.jpbazaar.controller;

import com.jpbazaar.dto.ApiResponse;
import com.jpbazaar.dto.request.ChangePasswordRequest;
import com.jpbazaar.dto.request.UpdateProfileRequest;
import com.jpbazaar.dto.response.UserResponse;
import com.jpbazaar.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Customer profile inspection, updates, and password changes")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/me", "/profile"})
    @Operation(summary = "Get current user profile", description = "Fetches the profile details of the currently authenticated user.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid JWT token")
    })
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentProfile(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponse response = userService.getCurrentProfile(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping({"/me", "/profile"})
    @Operation(summary = "Update current user profile", description = "Updates first name, last name, and phone number of the authenticated user.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error in submitted fields"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid JWT token")
    })
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        UserResponse response = userService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change password", description = "Changes the user password after verifying current password.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error or incorrect current password"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid JWT token")
    })
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }
}

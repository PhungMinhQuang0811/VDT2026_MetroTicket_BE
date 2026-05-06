package com.vdt.authservice.controller;

import com.vdt.authservice.dto.request.auth.ForgotPasswordRequest;
import com.vdt.authservice.dto.request.auth.LoginRequest;
import com.vdt.authservice.dto.request.user.RegisterRequest;
import com.vdt.authservice.dto.request.auth.ResetPasswordRequest;
import com.vdt.authservice.dto.response.auth.AuthResponse;
import com.vdt.authservice.dto.response.user.UserResponse;
import com.vdt.authservice.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import com.vdt.authservice.dto.response.ApiResponse;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return ApiResponse.<AuthResponse>builder()
                .result(authService.login(request, response))
                .build();
    }

    @GetMapping("/activate")
    public ApiResponse<Void> activate(@RequestParam String token) {
        authService.activateAccount(token);
        return ApiResponse.<Void>builder()
                .message("Account activated successfully!")
                .build();
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ApiResponse.<Void>builder()
                .message("Reset link sent to your email!")
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.<Void>builder()
                .message("Password reset successfully!")
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletResponse response) {
        authService.logout(response);
        return ApiResponse.<Void>builder()
                .message("Logged out successfully!")
                .build();
    }

    @PostMapping("/refresh-token")
    public ApiResponse<Void> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken, 
            HttpServletResponse response) {
        authService.refreshToken(refreshToken, response);
        return ApiResponse.<Void>builder()
                .message("Token refreshed")
                .build();
    }
}

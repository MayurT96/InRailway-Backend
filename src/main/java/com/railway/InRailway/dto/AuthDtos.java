package com.railway.InRailway.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public final class AuthDtos {
    private AuthDtos() {}
    public record RegisterRequest(String username, @NotBlank @Email String email, @NotBlank String password) {}
    public record LoginRequest(String username, String email, @NotBlank String password) {}
    public record RefreshRequest(@NotBlank String refreshToken) {}
    public record TokenResponse(String accessToken, String refreshToken, String username, String role) {}
}

package com.railway.InRailway.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public final class ProfileDtos {
    private ProfileDtos() {}

    public record Response(Long id, String username, String fullName, String email, String role,
                           String avatarUrl, Instant createdAt, Instant lastLogin, Instant updatedAt,
                           String accountStatus) {}

    public record UpdateRequest(@Size(min = 3, max = 80) String username,
                                @Size(max = 160) String fullName,
                                @Email String email,
                                @Size(min = 8, max = 100) String password) {}

    public record AvatarRequest(String avatarUrl) {}

    public record Notification(String id, String type, String title, String message,
                               Instant createdAt, boolean read) {}

    public record Session(Long id, String device, String browser, String ipAddress,
                          Instant loginTime, Instant lastActivity, boolean current) {}

    public record SecurityResponse(String username, String email, boolean passwordConfigured,
                                   Instant lastLogin, Instant updatedAt) {}

    public record NotificationRequest(@jakarta.validation.constraints.NotBlank String title,
                                      @jakarta.validation.constraints.NotBlank String message,
                                      @jakarta.validation.constraints.NotBlank String type) {}
    public record NotificationResponse(Long id, String title, String message, String type,
                                       boolean readStatus, Instant createdAt) {}
    public record SavedRouteRequest(@jakarta.validation.constraints.NotBlank String source,
                                   @jakarta.validation.constraints.NotBlank String destination) {}
    public record SavedRouteResponse(Long id, String source, String destination, Instant createdAt) {}
}
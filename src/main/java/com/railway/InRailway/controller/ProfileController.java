package com.railway.InRailway.controller;

import com.railway.InRailway.dto.ProfileDtos;
import com.railway.InRailway.service.ProfileService;
import com.railway.InRailway.service.AvatarStorageService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProfileController {
    private final ProfileService service;
    private final AvatarStorageService avatarStorage;

    public ProfileController(ProfileService service, AvatarStorageService avatarStorage) {
        this.service = service;
        this.avatarStorage = avatarStorage;
    }

    @GetMapping("/profile")
    public ProfileDtos.Response profile(Authentication authentication) {
        return service.profile(authentication.getName());
    }

    @PutMapping("/profile")
    public ProfileDtos.Response update(@Valid @RequestBody ProfileDtos.UpdateRequest request,
                                       Authentication authentication) {
        return service.update(authentication.getName(), request);
    }

    @PostMapping("/profile/avatar")
    public ProfileDtos.Response avatar(@RequestBody ProfileDtos.AvatarRequest request,
                                       Authentication authentication) {
        if (request.avatarUrl() == null || request.avatarUrl().isBlank()) {
            throw new com.railway.InRailway.exception.ApiException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "avatarUrl is required");
        }
        return service.avatar(authentication.getName(), request.avatarUrl());
    }
    @DeleteMapping("/profile/avatar")
    public ProfileDtos.Response removeAvatar(Authentication authentication) { return service.removeAvatar(authentication.getName()); }
    @PostMapping(value = "/profile/avatar", consumes = "multipart/form-data")
    public ProfileDtos.Response uploadAvatar(@RequestPart("avatar") MultipartFile avatar, Authentication authentication) {
        return service.avatar(authentication.getName(), avatarStorage.store(avatar));
    }

    @GetMapping("/notifications")
    public java.util.Map<String, Object> notifications(Authentication authentication) {
        String username = authentication.getName();
        return java.util.Map.of("items", service.notifications(username), "unreadCount", service.unreadCount(username));
    }

    @PostMapping("/notifications")
    public ProfileDtos.NotificationResponse createNotification(@Valid @RequestBody ProfileDtos.NotificationRequest request, Authentication authentication) { return service.createNotification(authentication.getName(), request); }
    @PutMapping("/notifications/read")
    public void markNotificationsRead(Authentication authentication) { service.markNotificationsRead(authentication.getName()); }
    @DeleteMapping("/notifications")
    public void deleteNotifications(Authentication authentication) { service.deleteNotifications(authentication.getName()); }

    @GetMapping("/routes/saved")
    public List<ProfileDtos.SavedRouteResponse> savedRoutes(Authentication authentication) { return service.savedRoutes(authentication.getName()); }
    @PostMapping("/routes/saved")
    public ProfileDtos.SavedRouteResponse saveRoute(@Valid @RequestBody ProfileDtos.SavedRouteRequest request, Authentication authentication) { return service.saveRoute(authentication.getName(), request); }
    @DeleteMapping("/routes/saved/{id}")
    public void deleteRoute(@PathVariable Long id, Authentication authentication) { service.deleteRoute(authentication.getName(), id); }

    @GetMapping("/profile/security")
    public ProfileDtos.SecurityResponse security(Authentication authentication) {
        return service.security(authentication.getName());
    }

    @GetMapping("/profile/sessions")
    public List<ProfileDtos.Session> sessions(Authentication authentication) {
        return service.sessions(authentication.getName());
    }
    @DeleteMapping("/profile/sessions/{id}")
    public void deleteSession(@PathVariable Long id, Authentication authentication) { service.deleteSession(authentication.getName(), id); }
}
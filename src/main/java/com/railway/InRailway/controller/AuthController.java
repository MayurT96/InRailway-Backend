package com.railway.InRailway.controller;

import com.railway.InRailway.dto.AuthDtos;
import com.railway.InRailway.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;
    public AuthController(AuthService service) { this.service = service; }
    @PostMapping("/register") public ResponseEntity<AuthDtos.TokenResponse> register(@Valid @RequestBody AuthDtos.RegisterRequest request) { return ResponseEntity.ok(service.register(request)); }
    @PostMapping("/signup") public ResponseEntity<AuthDtos.TokenResponse> signup(@Valid @RequestBody AuthDtos.RegisterRequest request) { return register(request); }
    @PostMapping("/login") public ResponseEntity<AuthDtos.TokenResponse> login(@Valid @RequestBody AuthDtos.LoginRequest request, HttpServletRequest httpRequest) { return ResponseEntity.ok(service.login(request, httpRequest)); }
    @PostMapping("/refresh") public ResponseEntity<AuthDtos.TokenResponse> refresh(@Valid @RequestBody AuthDtos.RefreshRequest request) { return ResponseEntity.ok(service.refresh(request.refreshToken())); }
}

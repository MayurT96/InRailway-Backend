package com.railway.InRailway.service;

import com.railway.InRailway.dto.AuthDtos;
import com.railway.InRailway.exception.ApiException;
import com.railway.InRailway.model.*;
import com.railway.InRailway.repository.UserRepository;
import com.railway.InRailway.repository.UserSessionRepository;
import com.railway.InRailway.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;

@Service
public class AuthService {
    private final UserRepository users; private final PasswordEncoder encoder; private final JwtService jwt; private final UserSessionRepository sessions;
    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt, UserSessionRepository sessions) { this.users = users; this.encoder = encoder; this.jwt = jwt; this.sessions = sessions; }
    public AuthDtos.TokenResponse register(AuthDtos.RegisterRequest r) { String username = r.username() == null || r.username().isBlank() ? r.email() : r.username(); if (users.existsByUsernameOrEmail(username, r.email())) throw new ApiException(HttpStatus.CONFLICT, "Username or email already exists"); User u = new User(); u.setUsername(username); u.setEmail(r.email()); u.setPassword(encoder.encode(r.password())); u.setRole(Role.USER); return tokens(users.save(u)); }
    public AuthDtos.TokenResponse login(AuthDtos.LoginRequest r, HttpServletRequest request) { String identifier = r.username() == null || r.username().isBlank() ? r.email() : r.username(); User u = users.findByUsername(identifier).or(() -> users.findByEmail(identifier)).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials")); if (!encoder.matches(r.password(), u.getPassword())) throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"); Instant now = Instant.now(); u.setLastLogin(now); users.save(u); UserSession session = new UserSession(); session.setUser(u); session.setDevice(request.getHeader("User-Agent")); session.setBrowser(request.getHeader("User-Agent")); session.setIpAddress(request.getRemoteAddr()); session.setLoginTime(now); session.setLastActivity(now); sessions.save(session); return tokens(u); }
    public AuthDtos.TokenResponse refresh(String refreshToken) { String username; try { username = jwt.username(refreshToken); } catch (RuntimeException e) { throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"); } return tokens(users.findByUsername(username).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"))); }
    private AuthDtos.TokenResponse tokens(User u) { return new AuthDtos.TokenResponse(jwt.accessToken(u), jwt.refreshToken(u), u.getUsername(), u.getRole().name()); }
}

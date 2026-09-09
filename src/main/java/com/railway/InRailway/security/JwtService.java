package com.railway.InRailway.security;

import com.railway.InRailway.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key; private final long accessMs; private final long refreshMs;
    public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.access-expiration-ms}") long accessMs, @Value("${app.jwt.refresh-expiration-ms}") long refreshMs) { key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); this.accessMs = accessMs; this.refreshMs = refreshMs; }
    public String accessToken(User user) { return token(user, accessMs); }
    public String refreshToken(User user) { return token(user, refreshMs); }
    private String token(User user, long expiry) { return Jwts.builder().setSubject(user.getUsername()).claim("role", user.getRole().name()).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + expiry)).signWith(key, SignatureAlgorithm.HS256).compact(); }
    public String username(String token) { return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject(); }
    public boolean valid(String token, UserDetails user) { try { return username(token).equals(user.getUsername()) && !Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration().before(new Date()); } catch (JwtException | IllegalArgumentException e) { return false; } }
}

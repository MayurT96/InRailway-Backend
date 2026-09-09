package com.railway.InRailway.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "user_sessions", indexes = @Index(name = "idx_sessions_user", columnList = "user_id"))
@Getter @Setter @NoArgsConstructor
public class UserSession {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Column(length = 120) private String device;
    @Column(length = 120) private String browser;
    @Column(length = 64) private String ipAddress;
    @Column(nullable = false) private Instant loginTime;
    @Column(nullable = false) private Instant lastActivity;
}
package com.railway.InRailway.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "saved_routes", indexes = @Index(name = "idx_saved_routes_user", columnList = "user_id"))
@Getter @Setter @NoArgsConstructor
public class SavedRoute {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Column(nullable = false, length = 120) private String source;
    @Column(nullable = false, length = 120) private String destination;
    @Column(nullable = false) private Instant createdAt;
    @PrePersist void onCreate() { if (createdAt == null) createdAt = Instant.now(); }
}
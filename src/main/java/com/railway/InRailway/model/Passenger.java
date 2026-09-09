package com.railway.InRailway.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "passengers")
@Getter @Setter @NoArgsConstructor
public class Passenger {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "booking_id", nullable = false) private Booking booking;
    @Column(nullable = false, length = 120) private String name;
    @Column(nullable = false) private Integer age;
    @Column(nullable = false, length = 20) private String gender;
    @Column(length = 30) private String berthPreference;
}

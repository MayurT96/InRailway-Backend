package com.railway.InRailway.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings", indexes = {@Index(name = "idx_booking_user", columnList = "user_id"), @Index(name = "idx_booking_pnr", columnList = "pnr_number")})
@Getter @Setter @NoArgsConstructor
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false) private User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "train_id", nullable = false) private Train train;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "source_station_id", nullable = false) private Station source;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "destination_station_id", nullable = false) private Station destination;
    @Column(name = "journey_date", nullable = false) private LocalDate journeyDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private BookingStatus bookingStatus = BookingStatus.CONFIRMED;
    @Column(nullable = false) private LocalDateTime bookingTime;
    @Column(name = "pnr_number", nullable = false, unique = true, length = 20) private String pnrNumber;
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true) private List<Passenger> passengers = new ArrayList<>();
}

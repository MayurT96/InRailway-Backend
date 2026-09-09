package com.railway.InRailway.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pnrs", uniqueConstraints = @UniqueConstraint(name = "uk_pnr_number", columnNames = "pnr_number"))
@Getter @Setter @NoArgsConstructor
public class PNR {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "pnr_number", nullable = false, length = 20) private String pnrNumber;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "booking_id", nullable = false, unique = true) private Booking booking;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private BookingStatus status;
}

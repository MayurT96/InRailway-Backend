package com.railway.InRailway.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "route_stations", uniqueConstraints = @UniqueConstraint(name = "uk_route_stop", columnNames = {"route_id", "stop_number"}))
@Getter @Setter @NoArgsConstructor
public class RouteStation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "route_id", nullable = false) private Route route;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "station_id", nullable = false) private Station station;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    @Column(name = "stop_number", nullable = false) private Integer stopNumber;
    @Column(name = "halt_duration_minutes") private Integer haltDurationMinutes = 0;
    @Column(name = "distance_from_source") private BigDecimal distanceFromSource = BigDecimal.ZERO;
}

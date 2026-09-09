package com.railway.InRailway.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
@Getter @Setter @NoArgsConstructor
public class Route {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "train_id", nullable = false, unique = true) private Train train;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "source_station_id", nullable = false) private Station source;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "destination_station_id", nullable = false) private Station destination;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal distance;
    @Column(nullable = false) private Duration duration;
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true) @OrderBy("stopNumber ASC") private List<RouteStation> stations = new ArrayList<>();
}

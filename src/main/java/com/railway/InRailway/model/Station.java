package com.railway.InRailway.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "stations", uniqueConstraints = @UniqueConstraint(name = "uk_station_code", columnNames = "station_code"), indexes = @Index(name = "idx_station_name", columnList = "station_name"))
@Getter @Setter @NoArgsConstructor
public class Station {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "station_code", nullable = false, length = 10) private String stationCode;
    @Column(name = "station_name", nullable = false, length = 120) private String stationName;
    @Column(nullable = false, length = 80) private String state;
    @Column(nullable = false, length = 40) private String zone;
    @Column(precision = 9, scale = 6) private BigDecimal latitude;
    @Column(precision = 9, scale = 6) private BigDecimal longitude;
    @Column(nullable = false) private boolean junction;
    @Column(nullable = false) private boolean active = true;
}

package com.railway.InRailway.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trains", uniqueConstraints = @UniqueConstraint(name = "uk_train_number", columnNames = "train_number"), indexes = @Index(name = "idx_train_route", columnList = "source_station_id,destination_station_id"))
@Getter @Setter @NoArgsConstructor
public class Train {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "train_number", nullable = false, length = 20) private String trainNumber;
    @Column(nullable = false, length = 150) private String trainName;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private TrainType trainType;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "source_station_id", nullable = false) private Station sourceStation;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "destination_station_id", nullable = false) private Station destinationStation;
    @Column(nullable = false, length = 30) private String runningDays;
    @Column(nullable = false) private boolean active = true;
}

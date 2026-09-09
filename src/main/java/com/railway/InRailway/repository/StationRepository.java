package com.railway.InRailway.repository;

import com.railway.InRailway.model.Station;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StationRepository extends JpaRepository<Station, Long> {
    Optional<Station> findByStationCodeIgnoreCase(String stationCode);
    Page<Station> findByActiveTrueAndStationNameContainingIgnoreCaseOrActiveTrueAndStationCodeContainingIgnoreCase(String name, String code, Pageable pageable);
}

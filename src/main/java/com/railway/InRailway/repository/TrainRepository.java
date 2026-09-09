package com.railway.InRailway.repository;

import com.railway.InRailway.model.Train;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TrainRepository extends JpaRepository<Train, Long> {
    Optional<Train> findByTrainNumber(String trainNumber);

    @Query("SELECT t FROM Train t WHERE t.active = true AND (" +
           "LOWER(t.trainName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(t.trainNumber) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(t.sourceStation.stationCode) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(t.sourceStation.stationName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(t.destinationStation.stationCode) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(t.destinationStation.stationName) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Train> searchByKeyword(@Param("q") String q, Pageable pageable);

    @Query("SELECT DISTINCT t FROM Train t " +
           "WHERE t.active = true AND (" +
           "  (" +
           "    (UPPER(t.sourceStation.stationCode) = UPPER(:src) OR LOWER(t.sourceStation.stationName) LIKE LOWER(CONCAT('%', :src, '%'))) AND " +
           "    (UPPER(t.destinationStation.stationCode) = UPPER(:dest) OR LOWER(t.destinationStation.stationName) LIKE LOWER(CONCAT('%', :dest, '%')))" +
           "  ) OR EXISTS (" +
           "    SELECT 1 FROM Route r " +
           "    JOIN r.stations s1 " +
           "    JOIN r.stations s2 " +
           "    WHERE r.train = t AND s1.stopNumber < s2.stopNumber AND (" +
           "      (UPPER(s1.station.stationCode) = UPPER(:src) OR LOWER(s1.station.stationName) LIKE LOWER(CONCAT('%', :src, '%'))) AND " +
           "      (UPPER(s2.station.stationCode) = UPPER(:dest) OR LOWER(s2.station.stationName) LIKE LOWER(CONCAT('%', :dest, '%')))" +
           "    )" +
           "  )" +
           ")")
    Page<Train> searchBetweenStations(@Param("src") String src, @Param("dest") String dest, Pageable pageable);

    Page<Train> findByActiveTrue(Pageable pageable);
}

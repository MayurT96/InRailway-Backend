package com.railway.InRailway.repository;

import com.railway.InRailway.model.RouteStation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteStationRepository extends JpaRepository<RouteStation, Long> {
    Optional<RouteStation> findByRouteIdAndStationId(Long routeId, Long stationId);
    Optional<RouteStation> findByRouteIdAndStopNumber(Long routeId, Integer stopNumber);
    java.util.List<RouteStation> findByRouteIdOrderByStopNumberAsc(Long routeId);
    long countByRouteId(Long routeId);
}

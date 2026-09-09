package com.railway.InRailway.repository;

import com.railway.InRailway.model.Route;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {
    Optional<Route> findByTrainTrainNumber(String trainNumber);
}

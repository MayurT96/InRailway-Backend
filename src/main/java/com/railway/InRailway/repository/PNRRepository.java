package com.railway.InRailway.repository;

import com.railway.InRailway.model.PNR;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PNRRepository extends JpaRepository<PNR, Long> {
    Optional<PNR> findByPnrNumber(String pnrNumber);
}

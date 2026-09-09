package com.railway.InRailway.repository;
import com.railway.InRailway.model.SavedRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface SavedRouteRepository extends JpaRepository<SavedRoute, Long> {
    List<SavedRoute> findByUserUsernameOrderByCreatedAtDesc(String username);
}
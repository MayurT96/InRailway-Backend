package com.railway.InRailway.repository;
import com.railway.InRailway.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    List<UserSession> findByUserUsernameOrderByLastActivityDesc(String username);
}
package com.railway.InRailway.repository;
import com.railway.InRailway.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserUsernameOrderByCreatedAtDesc(String username);
    long countByUserUsernameAndReadStatusFalse(String username);
}
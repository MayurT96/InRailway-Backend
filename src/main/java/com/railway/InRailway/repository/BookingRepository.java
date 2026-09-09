package com.railway.InRailway.repository;

import com.railway.InRailway.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByUserUsernameOrderByBookingTimeDesc(String username, Pageable pageable);
}

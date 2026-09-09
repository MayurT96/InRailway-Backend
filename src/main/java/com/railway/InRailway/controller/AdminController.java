package com.railway.InRailway.controller;

import com.railway.InRailway.repository.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/api/admin")
public class AdminController {
    private final StationRepository stations; private final TrainRepository trains; private final BookingRepository bookings; private final UserRepository users;
    public AdminController(StationRepository stations, TrainRepository trains, BookingRepository bookings, UserRepository users) { this.stations=stations; this.trains=trains; this.bookings=bookings; this.users=users; }
    @GetMapping("/analytics") public Map<String, Long> analytics() { return Map.of("stations", stations.count(), "trains", trains.count(), "bookings", bookings.count(), "users", users.count()); }
}

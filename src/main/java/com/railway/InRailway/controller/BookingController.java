package com.railway.InRailway.controller;

import com.railway.InRailway.dto.BookingDtos;
import com.railway.InRailway.service.BookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public BookingDtos.Response book(
            @RequestBody(required = false) BookingDtos.Request request,
            @RequestParam(required = false) Long trainId,
            @RequestParam(required = false) String passengerName,
            @RequestParam(required = false) Integer passengerAge,
            Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        if (request == null) {
            request = new BookingDtos.Request(trainId, null, null, null, passengerName, passengerAge, null);
        } else if (trainId != null && request.trainId() == null) {
            request = new BookingDtos.Request(trainId, request.sourceStationCode(), request.destinationStationCode(), request.journeyDate(), passengerName != null ? passengerName : request.passengerName(), passengerAge != null ? passengerAge : request.passengerAge(), request.passengers());
        }
        return service.book(request, username);
    }

    @GetMapping({"", "/my"})
    public Page<BookingDtos.Response> history(Pageable pageable, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        return service.history(username, pageable);
    }

    @PatchMapping("/{id}/cancel")
    public BookingDtos.Response cancel(@PathVariable Long id, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        return service.cancel(id, username);
    }
}

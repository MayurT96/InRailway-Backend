package com.railway.InRailway.controller;

import com.railway.InRailway.dto.BookingDtos;
import com.railway.InRailway.service.BookingService;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/pnr")
public class PNRController {
    private final BookingService service;
    public PNRController(BookingService service) { this.service = service; }
    @GetMapping("/{pnr}") public BookingDtos.Response search(@PathVariable String pnr) { return service.pnr(pnr); }
}

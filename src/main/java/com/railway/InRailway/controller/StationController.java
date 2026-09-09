package com.railway.InRailway.controller;

import com.railway.InRailway.dto.StationDtos;
import com.railway.InRailway.service.StationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/stations")
public class StationController {
    private final StationService service;
    public StationController(StationService service) { this.service = service; }
    @GetMapping public Page<StationDtos.Response> list(@RequestParam(defaultValue = "") String q, Pageable pageable) { return service.search(q, pageable); }
    @GetMapping("/search") public Page<StationDtos.Response> search(@RequestParam String q, Pageable pageable) { return service.search(q, pageable); }
    @PreAuthorize("hasRole('ADMIN')") @PostMapping public StationDtos.Response create(@Valid @RequestBody StationDtos.Request request) { return service.save(request, null); }
    @PreAuthorize("hasRole('ADMIN')") @PutMapping("/{id}") public StationDtos.Response update(@PathVariable Long id, @Valid @RequestBody StationDtos.Request request) { return service.save(request, id); }
    @PreAuthorize("hasRole('ADMIN')") @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { service.delete(id); }
}

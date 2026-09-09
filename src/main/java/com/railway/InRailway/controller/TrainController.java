package com.railway.InRailway.controller;

import com.railway.InRailway.dto.TrainDtos;
import com.railway.InRailway.service.TrainService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trains")
public class TrainController {
    private final TrainService service;

    public TrainController(TrainService service) {
        this.service = service;
    }

    @GetMapping
    public Page<TrainDtos.Response> list(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            Pageable pageable) {
        String effectiveSource = source != null ? source : from;
        String effectiveDest = destination != null ? destination : to;

        if (effectiveSource != null || effectiveDest != null) {
            return service.route(effectiveSource, effectiveDest, pageable);
        }
        return service.search(q, pageable);
    }

    @GetMapping({"/search", "/find"})
    public Page<TrainDtos.Response> search(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false, defaultValue = "") String q,
            Pageable pageable) {
        String effectiveSource = source != null ? source : from;
        String effectiveDest = destination != null ? destination : to;

        if (effectiveSource != null || effectiveDest != null) {
            return service.route(effectiveSource, effectiveDest, pageable);
        }
        return service.search(q, pageable);
    }

    @GetMapping("/{id}")
    public TrainDtos.Response getById(@PathVariable Long id) {
        return service.response(service.find(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public TrainDtos.Response create(@Valid @RequestBody TrainDtos.Request request) {
        return service.save(request, null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public TrainDtos.Response update(@PathVariable Long id, @Valid @RequestBody TrainDtos.Request request) {
        return service.save(request, id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

package com.railway.InRailway.service;

import com.railway.InRailway.dto.StationDtos;
import com.railway.InRailway.model.Station;
import com.railway.InRailway.repository.StationRepository;
import com.railway.InRailway.exception.ApiException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class StationService {
    private final StationRepository repository;
    public StationService(StationRepository repository) { this.repository = repository; }
    public Page<StationDtos.Response> search(String query, Pageable pageable) { return repository.findByActiveTrueAndStationNameContainingIgnoreCaseOrActiveTrueAndStationCodeContainingIgnoreCase(query, query, pageable).map(this::response); }
    public Station find(String code) { return repository.findByStationCodeIgnoreCase(code).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Station not found: " + code)); }
    public StationDtos.Response save(StationDtos.Request request, Long id) { Station s = id == null ? new Station() : repository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Station not found")); s.setStationCode(request.stationCode().toUpperCase()); s.setStationName(request.stationName()); s.setState(request.state()); s.setZone(request.zone()); s.setLatitude(request.latitude()); s.setLongitude(request.longitude()); s.setJunction(request.junction()); s.setActive(request.active()); return response(repository.save(s)); }
    public void delete(Long id) { Station s = repository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Station not found")); s.setActive(false); repository.save(s); }
    public StationDtos.Response response(Station s) { return new StationDtos.Response(s.getId(), s.getStationCode(), s.getStationName(), s.getState(), s.getZone(), s.getLatitude(), s.getLongitude(), s.isJunction(), s.isActive()); }
}

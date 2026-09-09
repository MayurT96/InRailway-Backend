package com.railway.InRailway.service;

import com.railway.InRailway.dto.TrainDtos;
import com.railway.InRailway.model.Train;
import com.railway.InRailway.repository.TrainRepository;
import com.railway.InRailway.exception.ApiException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainService {
    private final TrainRepository repository;
    private final StationService stations;

    public TrainService(TrainRepository repository, StationService stations) {
        this.repository = repository;
        this.stations = stations;
    }

    @Transactional(readOnly = true)
    public Page<TrainDtos.Response> search(String q, Pageable p) {
        if (q == null || q.trim().isEmpty()) {
            return repository.findByActiveTrue(p).map(this::response);
        }
        return repository.searchByKeyword(q.trim(), p).map(this::response);
    }

    @Transactional(readOnly = true)
    public Page<TrainDtos.Response> route(String source, String destination, Pageable p) {
        String src = source != null ? source.trim() : "";
        String dst = destination != null ? destination.trim() : "";

        if (src.isEmpty() && dst.isEmpty()) {
            return repository.findByActiveTrue(p).map(this::response);
        }
        if (src.isEmpty()) {
            return search(dst, p);
        }
        if (dst.isEmpty()) {
            return search(src, p);
        }
        return repository.searchBetweenStations(src, dst, p).map(this::response);
    }

    @Transactional
    public TrainDtos.Response save(TrainDtos.Request r, Long id) {
        Train t = id == null ? new Train() : repository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Train not found"));
        t.setTrainNumber(r.trainNumber());
        t.setTrainName(r.trainName());
        t.setTrainType(r.trainType());
        t.setSourceStation(stations.find(r.sourceStationCode()));
        t.setDestinationStation(stations.find(r.destinationStationCode()));
        t.setRunningDays(r.runningDays());
        t.setActive(r.active());
        return response(repository.save(t));
    }

    @Transactional
    public void delete(Long id) {
        Train t = repository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Train not found"));
        t.setActive(false);
        repository.save(t);
    }

    public Train find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Train not found"));
    }

    public TrainDtos.Response response(Train t) {
        return new TrainDtos.Response(
                t.getId(),
                t.getTrainNumber(),
                t.getTrainName(),
                t.getTrainType(),
                stations.response(t.getSourceStation()),
                stations.response(t.getDestinationStation()),
                t.getRunningDays(),
                t.isActive()
        );
    }
}

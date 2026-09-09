package com.railway.InRailway.dto;

import com.railway.InRailway.model.TrainType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class TrainDtos {
    private TrainDtos() {}
    public record Request(@NotBlank String trainNumber, @NotBlank String trainName, @NotNull TrainType trainType, @NotBlank String sourceStationCode, @NotBlank String destinationStationCode, @NotBlank String runningDays, boolean active) {}
    public record Response(Long id, String trainNumber, String trainName, TrainType trainType, StationDtos.Response sourceStation, StationDtos.Response destinationStation, String runningDays, boolean active) {}
}

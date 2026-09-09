package com.railway.InRailway.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class BookingDtos {
    private BookingDtos() {}
    public record PassengerRequest(String name, Integer age, String gender, String berthPreference) {}
    public record Request(
            @NotNull Long trainId,
            String sourceStationCode,
            String destinationStationCode,
            LocalDate journeyDate,
            String passengerName,
            Integer passengerAge,
            List<@Valid PassengerRequest> passengers
    ) {}
    public record Response(
            Long id,
            String pnrNumber,
            String trainNumber,
            String trainName,
            String source,
            String destination,
            LocalDate journeyDate,
            String status,
            LocalDateTime bookingTime,
            String passengerName,
            Integer passengerAge,
            List<PassengerRequest> passengers
    ) {}
}

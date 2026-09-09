package com.railway.InRailway.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public final class StationDtos {
    private StationDtos() {}
    public record Request(@NotBlank String stationCode, @NotBlank String stationName, @NotBlank String state, @NotBlank String zone, BigDecimal latitude, BigDecimal longitude, boolean junction, boolean active) {}
    public record Response(Long id, String stationCode, String stationName, String state, String zone, BigDecimal latitude, BigDecimal longitude, boolean junction, boolean active) {}
}

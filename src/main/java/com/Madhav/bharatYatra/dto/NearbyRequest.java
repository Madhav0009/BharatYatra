package com.Madhav.bharatYatra.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NearbyRequest {

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @Builder.Default
    private Double radiusKm = 500.0;

    @Builder.Default
    private Integer limit = 5;
}
package com.Madhav.bharatYatra.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;



@Data
public class TravelFromLocationRequest {
    @NotNull private Long placeId;
    private Double latitude;
    private Double longitude;
    private String city;   // if GPS unavailable
}
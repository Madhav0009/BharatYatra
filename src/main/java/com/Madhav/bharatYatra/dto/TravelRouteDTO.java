package com.Madhav.bharatYatra.dto;

import lombok.Builder;
import lombok.Data;

//══ TRAVEL ROUTE ══════════════════════════════════════

@Data @Builder
public class TravelRouteDTO {
 private Long id;
 private String fromCity;
 private String mode;
 private String duration;
 private String distance;
 private String routeNote;
 private String bookingUrl;
 private boolean fastest;   // computed field
}
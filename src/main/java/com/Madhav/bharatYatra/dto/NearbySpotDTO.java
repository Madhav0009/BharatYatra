package com.Madhav.bharatYatra.dto;

import lombok.Builder;
import lombok.Data;

//══ NEARBY SPOT ═══════════════════════════════════════

@Data @Builder
public class NearbySpotDTO {
 private Long id;
 private String icon;
 private String name;
 private String type;
 private String distanceKm;
 private String description;
 private String bookingUrl;
 private String buttonLabel;
}
package com.Madhav.bharatYatra.dto;

import lombok.Builder;
import lombok.Data;

//══ FACILITY ══════════════════════════════════════════

@Data @Builder
public class FacilityDTO {
 private Long id;
 private String icon;
 private String name;
 private String description;
 private String tag;
}
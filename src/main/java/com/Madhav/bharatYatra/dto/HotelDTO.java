package com.Madhav.bharatYatra.dto;

import lombok.Builder;
import lombok.Data;

//══ HOTEL ═══════


@Data @Builder
public class HotelDTO {
 private Long id;
 private String name;
 private Integer stars;
 private String description;
 private Double pricePerNight;
 private String emoji;
 private String bgGradient;
 private String bookingUrl;
}

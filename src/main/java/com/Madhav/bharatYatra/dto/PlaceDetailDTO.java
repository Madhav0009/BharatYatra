package com.Madhav.bharatYatra.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

//══ PLACE ═════════════════════════════════════════════


@Data @Builder
public class PlaceDetailDTO {
 private Long id;
 private String name;
 private String state;
 private String emoji;
 private String tagline;
 private String type;
 private String description;
 private String descriptionExtra;
 private String bgGradient;
 private Double latitude;
 private Double longitude;
 private String founded;
 private String nearestAirport;
 private String nearestRailway;
 private String nearestBusStand;
 private Double averageRating;
 private Integer totalReviews;
 private List<TimelineDTO> timeline;
 private List<FacilityDTO> facilities;
 private List<HotelDTO> hotels;
 private List<NearbySpotDTO> nearbySpots;
 private List<PlaceSummaryDTO> nextPlaces;  // nearest places
 private List<TravelRouteDTO> travelRoutes; // from user's city
}
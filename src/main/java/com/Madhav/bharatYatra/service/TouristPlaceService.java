package com.Madhav.bharatYatra.service;

import com.Madhav.bharatYatra.dto.FacilityDTO;
import com.Madhav.bharatYatra.dto.HotelDTO;
import com.Madhav.bharatYatra.dto.NearbySpotDTO;
import com.Madhav.bharatYatra.dto.PlaceDetailDTO;
import com.Madhav.bharatYatra.dto.PlaceSummaryDTO;
import com.Madhav.bharatYatra.dto.SearchResponse;
import com.Madhav.bharatYatra.dto.TimelineDTO;
import com.Madhav.bharatYatra.dto.TravelRouteDTO;
import com.Madhav.bharatYatra.exception.ResourceNotFoundException;
import com.Madhav.bharatYatra.model.TouristPlace;
import com.Madhav.bharatYatra.model.TravelRoute;
import com.Madhav.bharatYatra.repository.TouristPlaceRepository;
import com.Madhav.bharatYatra.repository.TravelRouteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TouristPlaceService {

    private final TouristPlaceRepository placeRepo;
    private final TravelRouteRepository routeRepo;

    // ───────────────── SEARCH ─────────────────

    public SearchResponse search(String query) {

        log.info("Searching places for: {}", query);

        List<TouristPlace> places =
                placeRepo.searchByNameOrState(query);

        List<PlaceSummaryDTO> dtos = places.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());

        return SearchResponse.builder()
                .places(dtos)
                .query(query)
                .totalResults(dtos.size())
                .build();
    }

    // ─────────────── PLACE DETAIL ───────────────

    @Transactional(readOnly = true)
    public PlaceDetailDTO getPlaceDetail(
            Long placeId,
            String fromCity,
            Double userLat,
            Double userLon
    ) {

        TouristPlace place = placeRepo.findById(placeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Place not found: " + placeId
                        )
                );

        String resolvedCity =
                resolveCity(fromCity, userLat, userLon);

        List<TravelRoute> routes =
                routeRepo.findByPlaceIdAndFromCityIgnoreCase(
                        placeId,
                        resolvedCity
                );

        // Find fastest route
        TravelRoute fastest = routes.stream()
                .filter(r ->
                        r.getDuration() != null &&
                        !r.getDuration().equals("N/A")
                )
                .min((a, b) ->
                        Integer.compare(
                                parseHours(a.getDuration()),
                                parseHours(b.getDuration())
                        )
                )
                .orElse(null);

        // Convert routes to DTO
        List<TravelRouteDTO> routeDTOs = routes.stream()
                .map(r -> toRouteDTO(r, r.equals(fastest)))
                .collect(Collectors.toList());

        // Find nearby places
        List<TouristPlace> nearestPlaces =
                placeRepo.findNearby(
                                place.getLatitude(),
                                place.getLongitude(),
                                2000.0,
                                4
                        ).stream()
                        .filter(p -> !p.getId().equals(placeId))
                        .limit(3)
                        .collect(Collectors.toList());

        return PlaceDetailDTO.builder()

                .id(place.getId())
                .name(place.getName())
                .state(place.getState())
                .emoji(place.getEmoji())
                .tagline(place.getTagline())

                .type(
                        place.getType() != null
                                ? place.getType().name()
                                : null
                )

                .description(place.getDescription())
                .descriptionExtra(place.getDescriptionExtra())
                .bgGradient(place.getBgGradient())

                .latitude(place.getLatitude())
                .longitude(place.getLongitude())

                .founded(place.getFounded())

                .nearestAirport(place.getNearestAirport())
                .nearestRailway(place.getNearestRailway())
                .nearestBusStand(place.getNearestBusStand())

                .averageRating(place.getAverageRating())
                .totalReviews(place.getTotalReviews())

                // Timeline
                .timeline(
                        place.getTimeline().stream()
                                .map(t -> TimelineDTO.builder()
                                        .year(t.getYear())
                                        .description(t.getDescription())
                                        .build())
                                .collect(Collectors.toList())
                )

                // Facilities
                .facilities(
                        place.getFacilities().stream()
                                .map(f -> FacilityDTO.builder()
                                        .id(f.getId())
                                        .icon(f.getIcon())
                                        .name(f.getName())
                                        .description(f.getDescription())
                                        .tag(f.getTag())
                                        .build())
                                .collect(Collectors.toList())
                )

                // Hotels
                .hotels(
                        place.getHotels().stream()
                                .map(h -> HotelDTO.builder()
                                        .id(h.getId())
                                        .name(h.getName())
                                        .stars(h.getStars())
                                        .description(h.getDescription())
                                        .pricePerNight(h.getPricePerNight())
                                        .emoji(h.getEmoji())
                                        .bgGradient(h.getBgGradient())
                                        .bookingUrl(h.getBookingUrl())
                                        .build())
                                .collect(Collectors.toList())
                )

                // Nearby Spots
                .nearbySpots(
                        place.getNearbySpots().stream()
                                .map(s -> NearbySpotDTO.builder()
                                        .id(s.getId())
                                        .icon(s.getIcon())
                                        .name(s.getName())

                                        .type(
                                                s.getType() != null
                                                        ? s.getType().name()
                                                        : null
                                        )

                                        .distanceKm(s.getDistanceKm())
                                        .description(s.getDescription())
                                        .bookingUrl(s.getBookingUrl())
                                        .buttonLabel(s.getButtonLabel())
                                        .build())
                                .collect(Collectors.toList())
                )

                // Routes
                .travelRoutes(routeDTOs)

                // Next Places
                .nextPlaces(
                        nearestPlaces.stream()
                                .map(this::toSummaryDTO)
                                .collect(Collectors.toList())
                )

                .build();
    }

    // ─────────────── POPULAR PLACES ───────────────

    public List<PlaceSummaryDTO> getPopularPlaces() {

        return placeRepo.findTop10ByOrderByAverageRatingDesc()
                .stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    // ─────────────── NEARBY PLACES ───────────────

    public List<PlaceSummaryDTO> getNearby(
            Double lat,
            Double lon,
            Double radiusKm,
            Integer limit
    ) {

        return placeRepo.findNearby(
                        lat,
                        lon,
                        radiusKm,
                        limit
                )
                .stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    // ─────────────── ROUTES ───────────────

    public List<TravelRouteDTO> getRoutesFromCity(
            Long placeId,
            String city
    ) {

        List<TravelRoute> routes =
                routeRepo.findByPlaceIdAndFromCityIgnoreCase(
                        placeId,
                        city
                );

        TravelRoute fastest = routes.stream()
                .filter(r ->
                        r.getDuration() != null &&
                        !r.getDuration().equals("N/A")
                )
                .min((a, b) ->
                        Integer.compare(
                                parseHours(a.getDuration()),
                                parseHours(b.getDuration())
                        )
                )
                .orElse(null);

        return routes.stream()
                .map(r -> toRouteDTO(r, r.equals(fastest)))
                .collect(Collectors.toList());
    }

    // ─────────────── HELPERS ───────────────

    private PlaceSummaryDTO toSummaryDTO(TouristPlace p) {

        return PlaceSummaryDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .state(p.getState())
                .emoji(p.getEmoji())
                .tagline(p.getTagline())

                .type(
                        p.getType() != null
                                ? p.getType().name()
                                : null
                )

                .averageRating(p.getAverageRating())
                .totalReviews(p.getTotalReviews())
                .bgGradient(p.getBgGradient())
                .build();
    }

    private TravelRouteDTO toRouteDTO(
            TravelRoute r,
            boolean fastest
    ) {

        return TravelRouteDTO.builder()
                .id(r.getId())
                .fromCity(r.getFromCity())

                .mode(
                        r.getMode() != null
                                ? r.getMode().name()
                                : null
                )

                .duration(r.getDuration())
                .distance(r.getDistance())
                .routeNote(r.getRouteNote())
                .bookingUrl(r.getBookingUrl())
                .fastest(fastest)
                .build();
    }

    private String resolveCity(
            String providedCity,
            Double lat,
            Double lon
    ) {

        if (providedCity != null &&
                !providedCity.isBlank()) {

            return providedCity;
        }

        if (lat != null && lon != null) {

            // Bengaluru
            if (lat >= 12.7 && lat <= 13.2 &&
                    lon >= 77.3 && lon <= 77.8) {
                return "Bengaluru";
            }

            // Mumbai
            if (lat >= 18.8 && lat <= 19.3 &&
                    lon >= 72.7 && lon <= 73.1) {
                return "Mumbai";
            }

            // Delhi
            if (lat >= 28.4 && lat <= 28.9 &&
                    lon >= 76.8 && lon <= 77.4) {
                return "Delhi";
            }

            // Hyderabad
            if (lat >= 17.2 && lat <= 17.6 &&
                    lon >= 78.2 && lon <= 78.7) {
                return "Hyderabad";
            }

            // Kolkata
            if (lat >= 22.4 && lat <= 23.1 &&
                    lon >= 88.1 && lon <= 88.6) {
                return "Kolkata";
            }
        }

        return "Bengaluru";
    }

    private int parseHours(String duration) {

        if (duration == null ||
                duration.equals("N/A")) {

            return 999;
        }

        java.util.regex.Matcher matcher =
                java.util.regex.Pattern
                        .compile("(\\d+)h")
                        .matcher(duration);

        return matcher.find()
                ? Integer.parseInt(matcher.group(1))
                : 999;
    }
}
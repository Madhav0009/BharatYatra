package com.Madhav.bharatYatra.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Madhav.bharatYatra.dto.PlaceDetailDTO;
import com.Madhav.bharatYatra.dto.PlaceSummaryDTO;
import com.Madhav.bharatYatra.dto.SearchResponse;
import com.Madhav.bharatYatra.dto.TravelRouteDTO;
import com.Madhav.bharatYatra.service.TouristPlaceService;

import lombok.RequiredArgsConstructor;


//PLACE CONTROLLER  →  /api/places

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlaceController {

 private final TouristPlaceService placeService;

 /**
  * GET /api/places/search?query=hampi
  * Live search as user types
  */
 @GetMapping("/search")
 public ResponseEntity<SearchResponse> search(@RequestParam String query) {
     return ResponseEntity.ok(placeService.search(query));
 }

 /**
  * GET /api/places/popular
  * Home page popular destinations
  */
 @GetMapping("/popular")
 public ResponseEntity<List<PlaceSummaryDTO>> getPopular() {
     return ResponseEntity.ok(placeService.getPopularPlaces());
 }

 /**
  * GET /api/places/{id}?city=Bengaluru&lat=12.97&lon=77.59
  * Full place detail + travel routes from user's location
  */
 @GetMapping("/{id}")
 public ResponseEntity<PlaceDetailDTO> getDetail(
         @PathVariable Long id,
         @RequestParam(required = false) String city,
         @RequestParam(required = false) Double lat,
         @RequestParam(required = false) Double lon) {
     return ResponseEntity.ok(placeService.getPlaceDetail(id, city, lat, lon));
 }

 /**
  * GET /api/places/nearby?lat=12.97&lon=77.59&radiusKm=500&limit=5
  * Find nearest tourist places from user's GPS
  */
 @GetMapping("/nearby")
 public ResponseEntity<List<PlaceSummaryDTO>> getNearby(
         @RequestParam Double lat,
         @RequestParam Double lon,
         @RequestParam(defaultValue = "500") Double radiusKm,
         @RequestParam(defaultValue = "5") Integer limit) {
     return ResponseEntity.ok(placeService.getNearby(lat, lon, radiusKm, limit));
 }

 /**
  * GET /api/places/{id}/routes?city=Bengaluru
  * Travel routes from a specific city to this place
  */
 @GetMapping("/{id}/routes")
 public ResponseEntity<List<TravelRouteDTO>> getRoutes(
         @PathVariable Long id,
         @RequestParam String city) {
     return ResponseEntity.ok(placeService.getRoutesFromCity(id, city));
 }
}

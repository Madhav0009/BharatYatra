package com.Madhav.bharatYatra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Madhav.bharatYatra.model.Hotel;



//── HOTEL ──────────────────────────────────────────────
@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
 List<Hotel> findByPlaceId(Long placeId);
 List<Hotel> findByPlaceIdAndStarsGreaterThanEqual(Long placeId, Integer minStars);
 List<Hotel> findByPlaceIdAndPricePerNightBetween(Long placeId, Double min, Double max);


}

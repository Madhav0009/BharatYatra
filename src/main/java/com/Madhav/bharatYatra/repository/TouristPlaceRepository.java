package com.Madhav.bharatYatra.repository;


import com.Madhav.bharatYatra.model.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// ── TOURIST PLACE ──────────────────────────────────────
@Repository
public interface TouristPlaceRepository extends JpaRepository<TouristPlace, Long> {

    // Full-text search by name or state
    @Query("SELECT p FROM TouristPlace p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.state) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.tagline) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<TouristPlace> searchByNameOrState(@Param("query") String query);

    // Find by type
    List<TouristPlace> findByType(TouristPlace.PlaceType type);

    // Top-rated places
    List<TouristPlace> findTop10ByOrderByAverageRatingDesc();

    // Find by state
    List<TouristPlace> findByStateIgnoreCase(String state);

    // Nearby places (within radius using Haversine formula)
    @Query(value = """
        SELECT *, (
          6371 * ACOS(
            COS(RADIANS(:lat)) * COS(RADIANS(latitude)) *
            COS(RADIANS(longitude) - RADIANS(:lon)) +
            SIN(RADIANS(:lat)) * SIN(RADIANS(latitude))
          )
        ) AS distance
        FROM tourist_places
        HAVING distance < :radiusKm
        ORDER BY distance
        LIMIT :limit
        """, nativeQuery = true)
    List<TouristPlace> findNearby(
        @Param("lat") double lat,
        @Param("lon") double lon,
        @Param("radiusKm") double radiusKm,
        @Param("limit") int limit
    );

    Optional<TouristPlace> findByNameIgnoreCase(String name);
}




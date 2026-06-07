package com.Madhav.bharatYatra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Madhav.bharatYatra.model.TravelRoute;

// ── TRAVEL ROUTE ───────────────────────────────────────

@Repository
public interface TravelRouteRepository
        extends JpaRepository<TravelRoute, Long> {

    List<TravelRoute> findByPlaceIdAndFromCityIgnoreCase(
            Long placeId,
            String fromCity
    );

    List<TravelRoute> findByPlaceId(Long placeId);

    List<TravelRoute> findByFromCityIgnoreCaseAndMode(
            String fromCity,
            TravelRoute.TransportMode mode
    );
}
package com.Madhav.bharatYatra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Madhav.bharatYatra.model.NearbySpot;


//── NEARBY SPOT ────────────────────────────────────────
@Repository
public interface NearbySpotRepository extends JpaRepository<NearbySpot, Long> {
 List<NearbySpot> findByPlaceId(Long placeId);
 List<NearbySpot> findByPlaceIdAndType(Long placeId, NearbySpot.SpotType type);
}
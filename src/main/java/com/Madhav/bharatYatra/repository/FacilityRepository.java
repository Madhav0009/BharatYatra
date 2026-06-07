package com.Madhav.bharatYatra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Madhav.bharatYatra.model.Facility;


//── FACILITY ───────────────────────────────────────────
@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {
 List<Facility> findByPlaceId(Long placeId);
}  
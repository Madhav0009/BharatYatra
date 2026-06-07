package com.Madhav.bharatYatra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Madhav.bharatYatra.model.HistoryTimeline;



//── HISTORY TIMELINE ───────────────────────────────────
@Repository
public interface HistoryTimelineRepository extends JpaRepository<HistoryTimeline, Long> {
 List<HistoryTimeline> findByPlaceIdOrderBySortOrderAsc(Long placeId);
}
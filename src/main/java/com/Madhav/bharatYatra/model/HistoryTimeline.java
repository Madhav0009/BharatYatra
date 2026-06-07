package com.Madhav.bharatYatra.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//══════════════════════════════════════════
//HISTORY TIMELINE
//══════════════════════════════════════════
@Entity
@Table(name = "history_timeline")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HistoryTimeline {

 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "place_id", nullable = false)
 private TouristPlace place;

 private String year;

 @Column(columnDefinition = "TEXT")
 private String description;

 private Integer sortOrder;
}


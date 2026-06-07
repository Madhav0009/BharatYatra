package com.Madhav.bharatYatra.model;

import jakarta.persistence.*;

import lombok.*;

// ══════════════════════════════════════════
// TRAVEL ROUTE (from city → place)
// ══════════════════════════════════════════

@Entity
@Table(name = "travel_routes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private TouristPlace place;

    private String fromCity;

    @Enumerated(EnumType.STRING)
    private TransportMode mode;

    private String duration;

    private String distance;

    private String routeNote;

    private String bookingUrl;

    public enum TransportMode {
        TRAIN,
        FLIGHT,
        BUS,
        ROAD
    }
}
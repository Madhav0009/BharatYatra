package com.Madhav.bharatYatra.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tourist_places")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TouristPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String state;

    @Column
    private String emoji;

    @Column
    private String tagline;

    @Enumerated(EnumType.STRING)
    private PlaceType type;        

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String descriptionExtra;

    @Column
    private String bgGradient;     

    // Location
    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    // Quick info
    @Column
    private String founded;

    @Column
    private String nearestAirport;

    @Column
    private String nearestRailway;

    @Column
    private String nearestBusStand;

    // Rating (computed from reviews)
    @Column
    @Builder.Default
    private Double averageRating = 0.0;

    @Column
    @Builder.Default
    private Integer totalReviews = 0;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // Relationships
    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MediaUpload> media = new ArrayList<>();

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Blog> blogs = new ArrayList<>();

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Facility> facilities = new ArrayList<>();

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<NearbySpot> nearbySpots = new ArrayList<>();

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Hotel> hotels = new ArrayList<>();

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<HistoryTimeline> timeline = new ArrayList<>();

    public enum PlaceType {
        HERITAGE, BEACH, SPIRITUAL, ADVENTURE, NATURE, WILDLIFE, PILGRIMAGE, HILL_STATION
    }
}


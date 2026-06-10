package com.Madhav.bharatYatra.model;



import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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


//NEARBY SPOT

@Entity
@Table(name = "nearby_spots")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NearbySpot {

 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "place_id", nullable = false)
 private TouristPlace place;

 private String icon;
 private String name;

 @Enumerated(EnumType.STRING)
 private SpotType type;   
 private String distanceKm;
 private String description;
 private String bookingUrl;
 private String buttonLabel;

 public enum SpotType {
     HOSPITAL, BUS_STAND, RAILWAY, AIRPORT, FOOD, HOTEL, FUEL, ATTRACTION
 }
}
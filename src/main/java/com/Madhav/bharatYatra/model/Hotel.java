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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//HOTEL

@Entity
@Table(name = "hotels")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Hotel {

 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "place_id", nullable = false)
 private TouristPlace place;

 @NotBlank
 private String name;

 @Min(1) @Max(5)
 private Integer stars;

 @Column(columnDefinition = "TEXT")
 private String description;

 private Double pricePerNight;

 private String emoji;

 private String bgGradient;

 private String bookingUrl;  
}

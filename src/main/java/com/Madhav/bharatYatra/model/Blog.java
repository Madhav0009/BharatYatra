package com.Madhav.bharatYatra.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//BLOG

@Entity
@Table(name = "blogs")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Blog {

 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "place_id", nullable = false)
 private TouristPlace place;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "user_id", nullable = false)
 private User user;

 @NotBlank
 private String title;

 private String category; 

 @Column(columnDefinition = "LONGTEXT")
 private String content;

 private String emoji;

 private String bgGradient;

 private String thumbnailUrl;

 @Builder.Default
 private Boolean published = false;

 @CreatedDate
 @Column(updatable = false)
 private LocalDateTime createdAt;
}

package com.Madhav.bharatYatra.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

// ══════════════════════════════════════════
// MEDIA UPLOAD (Photos & Videos)
// ══════════════════════════════════════════

@Entity
@Table(name = "media_uploads")
@EntityListeners(AuditingEntityListener.class)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MediaUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tourist Place
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private TouristPlace place;

    // Uploaded User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // File URL
    @NotBlank
    private String fileUrl;

    // Original file name
    private String fileName;

    // File size
    private Long fileSizeBytes;

    // PHOTO or VIDEO
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    // Caption
    private String caption;

    // Approval status
    @Builder.Default
    private Boolean approved = true;

    // Upload time
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime uploadedAt;

    // ENUM
    public enum MediaType {
        PHOTO,
        VIDEO
    }
}
package com.Madhav.bharatYatra.dto;

import lombok.Builder;
import lombok.Data;


//══ PLACE ════════


@Data @Builder
public class PlaceSummaryDTO {
    private Long id;
    private String name;
    private String state;
    private String emoji;
    private String tagline;
    private String type;
    private Double averageRating;
    private Integer totalReviews;
    private String bgGradient;
}

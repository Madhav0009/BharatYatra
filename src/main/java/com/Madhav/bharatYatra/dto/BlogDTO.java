package com.Madhav.bharatYatra.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

//══ BLOG ══════════════════════════════════════════════


@Data @Builder
public class BlogDTO {
    private Long id;
    private String title;
    private String category;
    private String content;
    private String emoji;
    private String bgGradient;
    private String thumbnailUrl;
    private String authorName;
    private LocalDateTime createdAt;
}
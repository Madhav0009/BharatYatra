package com.Madhav.bharatYatra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogRequest {

    @NotNull
    private Long placeId;

    @NotBlank
    private String title;

    private String category;

    @NotBlank
    private String content;

    private String emoji;
}
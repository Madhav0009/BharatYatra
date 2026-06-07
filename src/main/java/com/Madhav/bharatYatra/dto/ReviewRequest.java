package com.Madhav.bharatYatra.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {

    @NotNull
    private Long placeId;

    @Min(1)
    @Max(5)
    @NotNull
    private Integer rating;

    @NotBlank
    @Size(min = 10, max = 1000)
    private String comment;

    private String visitDate;
}
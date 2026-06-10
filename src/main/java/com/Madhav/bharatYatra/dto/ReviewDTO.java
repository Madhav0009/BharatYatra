package com.Madhav.bharatYatra.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

//══ REVIEW ═══════



@Data @Builder
public class ReviewDTO {
 private Long id;
 private Integer rating;
 private String comment;
 private String visitDate;
 private String userName;
 private String userProfilePic;
 private LocalDateTime createdAt;
}
package com.Madhav.bharatYatra.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

//══ MEDIA ════

@Data @Builder
public class MediaDTO {
 private Long id;
 private String fileUrl;
 private String fileName;
 private String mediaType;
 private String caption;
 private String uploaderName;
 private LocalDateTime uploadedAt;
}

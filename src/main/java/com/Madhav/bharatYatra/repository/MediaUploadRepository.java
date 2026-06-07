package com.Madhav.bharatYatra.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Madhav.bharatYatra.model.MediaUpload;

//── MEDIA UPLOAD ───────────────────────────────────────
@Repository
public interface MediaUploadRepository extends JpaRepository<MediaUpload, Long> {
 Page<MediaUpload> findByPlaceId(Long placeId, Pageable pageable);
 Page<MediaUpload> findByPlaceIdAndMediaType(Long placeId, MediaUpload.MediaType type, Pageable pageable);
 List<MediaUpload> findByUserId(Long userId);
}
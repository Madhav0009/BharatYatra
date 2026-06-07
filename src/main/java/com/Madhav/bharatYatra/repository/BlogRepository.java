package com.Madhav.bharatYatra.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Madhav.bharatYatra.model.Blog;

// ── BLOG ───────────────────────────────────────────────

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

    Page<Blog> findByPlaceIdAndPublished(
            Long placeId,
            Boolean published,
            Pageable pageable
    );

    List<Blog> findByUserId(Long userId);

    Page<Blog> findByPublished(
            Boolean published,
            Pageable pageable
    );
}
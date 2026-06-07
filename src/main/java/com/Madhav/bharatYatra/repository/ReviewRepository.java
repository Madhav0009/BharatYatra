package com.Madhav.bharatYatra.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Madhav.bharatYatra.model.Review;

public interface ReviewRepository
        extends JpaRepository<Review, Long> {

    Page<Review> findByPlaceId(
            Long placeId,
            Pageable pageable
    );
 
    List<Review> findByUserId(Long userId);

    boolean existsByPlaceIdAndUserId(
            Long placeId,
            Long userId
    );

    @Query("""
        SELECT AVG(r.rating)
        FROM Review r
        WHERE r.place.id = :placeId
    """)
    Double getAverageRatingForPlace(
            @Param("placeId") Long placeId
    );

    @Query("""
        SELECT COUNT(r)
        FROM Review r
        WHERE r.place.id = :placeId
    """)
    Long countByPlaceId(
            @Param("placeId") Long placeId
    );
}
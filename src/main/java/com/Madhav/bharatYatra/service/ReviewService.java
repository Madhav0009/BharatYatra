package com.Madhav.bharatYatra.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Madhav.bharatYatra.dto.ReviewDTO;
import com.Madhav.bharatYatra.dto.ReviewRequest;
import com.Madhav.bharatYatra.exception.ResourceNotFoundException;
import com.Madhav.bharatYatra.model.Review;
import com.Madhav.bharatYatra.model.TouristPlace;
import com.Madhav.bharatYatra.model.User;
import com.Madhav.bharatYatra.repository.ReviewRepository;
import com.Madhav.bharatYatra.repository.TouristPlaceRepository;
import com.Madhav.bharatYatra.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//══════════════════════════════════════════
// REVIEW SERVICE
//══════════════════════════════════════════

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final TouristPlaceRepository placeRepo;
    private final UserRepository userRepo;

    @Transactional
    public ReviewDTO addReview(
            ReviewRequest req,
            String userEmail
    ) {

        TouristPlace place = placeRepo.findById(req.getPlaceId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Place not found")
                );

        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );

        if (reviewRepo.existsByPlaceIdAndUserId(
                req.getPlaceId(),
                user.getId()
        )) {

            throw new IllegalStateException(
                    "You have already reviewed this place"
            );
        }

        Review review = Review.builder()
                .place(place)
                .user(user)
                .rating(req.getRating())
                .comment(req.getComment())
                .visitDate(req.getVisitDate())
                .build();

        review = reviewRepo.save(review);

        // Update place average rating

        updatePlaceRating(place);

        return toDTO(review);
    }

    public Page<ReviewDTO> getReviews(
            Long placeId,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return reviewRepo.findByPlaceId(
                placeId,
                pageable
        ).map(this::toDTO);
    }

    @Transactional
    public void deleteReview(
            Long reviewId,
            String userEmail
    ) {

        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Review not found")
                );

        if (!review.getUser().getEmail().equals(userEmail)) {
            throw new SecurityException(
                    "Not authorized to delete this review"
            );
        }

        reviewRepo.delete(review);

        updatePlaceRating(review.getPlace());
    }

    private void updatePlaceRating(TouristPlace place) {

        Double avg =
                reviewRepo.getAverageRatingForPlace(place.getId());

        Long count =
                reviewRepo.countByPlaceId(place.getId());

        place.setAverageRating(
                avg != null
                        ? Math.round(avg * 10.0) / 10.0
                        : 0.0
        );

        place.setTotalReviews(count.intValue());

        placeRepo.save(place);
    }

    private ReviewDTO toDTO(Review r) {

        return ReviewDTO.builder()
                .id(r.getId())
                .rating(r.getRating())
                .comment(r.getComment())
                .visitDate(r.getVisitDate())
                .userName(r.getUser().getFullName())
                .userProfilePic(
                        r.getUser().getProfilePictureUrl()
                )
                .createdAt(r.getCreatedAt())
                .build();
    }
}
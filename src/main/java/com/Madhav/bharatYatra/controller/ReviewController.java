package com.Madhav.bharatYatra.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Madhav.bharatYatra.dto.ReviewDTO;
import com.Madhav.bharatYatra.dto.ReviewRequest;
import com.Madhav.bharatYatra.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


//REVIEW CONTROLLER  →  /api/reviews

@RestController
@RequestMapping("/api/reviews") 
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewController {

 private final ReviewService reviewService;
 
 /*  GET /api/reviews/place/{placeId}?page=0&size=10 */
 
 @GetMapping("/place/{placeId}")
 public ResponseEntity<Page<ReviewDTO>> getReviews(
         @PathVariable Long placeId,
         @RequestParam(defaultValue = "0") int page,
         @RequestParam(defaultValue = "10") int size) {
     return ResponseEntity.ok(reviewService.getReviews(placeId, page, size));
 }

 /* POST /api/reviews
    Requires JWT auth  */
 
 @PostMapping
 public ResponseEntity<ReviewDTO> addReview(
         @Valid @RequestBody ReviewRequest req,
         @AuthenticationPrincipal UserDetails user) {
     return ResponseEntity.status(HttpStatus.CREATED)
         .body(reviewService.addReview(req, user.getUsername()));
 }

 /*  DELETE /api/reviews/{id}
      Only the review author can delete  */
 
 @DeleteMapping("/{id}")
 public ResponseEntity<Void> deleteReview(
         @PathVariable Long id,
         @AuthenticationPrincipal UserDetails user) {
     reviewService.deleteReview(id, user.getUsername());
     return ResponseEntity.noContent().build();
 }
}
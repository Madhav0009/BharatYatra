package com.Madhav.bharatYatra.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Madhav.bharatYatra.dto.BlogDTO;
import com.Madhav.bharatYatra.dto.BlogRequest;
import com.Madhav.bharatYatra.service.BlogService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;



//══════════════════════════════════════════════════════
//BLOG CONTROLLER  →  /api/blogs
//══════════════════════════════════════════════════════
@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BlogController {

 private final BlogService blogService;

// @PostMapping
// public ResponseEntity<BlogDTO> createBlog(
//         @Valid @RequestBody BlogRequest req,
//         @AuthenticationPrincipal UserDetails user) {
//
//     System.out.println("========== BLOG HIT ==========");
//     System.out.println(req);
//     System.out.println(user);
//
//     return ResponseEntity.status(HttpStatus.CREATED)
//             .body(blogService.createBlog(req, user.getUsername()));
// }
 
 /**
  * GET /api/blogs/place/{placeId}?page=0&size=10
  */
 @GetMapping("/place/{placeId}")
 public ResponseEntity<Page<BlogDTO>> getBlogsForPlace(
         @PathVariable Long placeId,
         @RequestParam(defaultValue = "0") int page,
         @RequestParam(defaultValue = "10") int size) {
     return ResponseEntity.ok(blogService.getBlogsForPlace(placeId, page, size));
 }

 /**
  * POST /api/blogs
  * Requires JWT auth
  */
 @PostMapping
 public ResponseEntity<BlogDTO> createBlog(
         @Valid @RequestBody BlogRequest req,
         @AuthenticationPrincipal UserDetails user) {
     return ResponseEntity.status(HttpStatus.CREATED)
         .body(blogService.createBlog(req, user.getUsername()));
 }
// @PostMapping
// public ResponseEntity<BlogDTO> createBlog(
//         @Valid @RequestBody BlogRequest req,
//         @AuthenticationPrincipal UserDetails user) {
//
//     System.out.println("=========== BLOG HIT ===========");
//     System.out.println("USER = " + user);
//     System.out.println("PLACE ID = " + req.getPlaceId());
//
//     return ResponseEntity.status(HttpStatus.CREATED)
//             .body(blogService.createBlog(req, user.getUsername()));
// }
}


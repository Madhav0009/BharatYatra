package com.Madhav.bharatYatra.controller;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Madhav.bharatYatra.dto.MediaDTO;
import com.Madhav.bharatYatra.service.MediaService;

import lombok.RequiredArgsConstructor;


//MEDIA CONTROLLER  →  /api/media

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MediaController {

 private final MediaService mediaService;

 /**
  * POST /api/media/upload
  * Multipart: file + placeId + optional caption
  * Requires JWT auth
  */
 @PostMapping("/upload")
 public ResponseEntity<MediaDTO> upload(
         @RequestParam("file") MultipartFile file,
         @RequestParam("placeId") Long placeId,
         @RequestParam(value = "caption", required = false) String caption,
         @AuthenticationPrincipal UserDetails user) throws IOException {
     return ResponseEntity.status(HttpStatus.CREATED)
         .body(mediaService.upload(placeId, file, caption, user.getUsername()));
 }

 /**
  * GET /api/media/place/{placeId}?type=PHOTO&page=0&size=20
  */
 @GetMapping("/place/{placeId}")
 public ResponseEntity<Page<MediaDTO>> getMedia(
         @PathVariable Long placeId,
         @RequestParam(required = false) String type,
         @RequestParam(defaultValue = "0") int page,
         @RequestParam(defaultValue = "20") int size) {
     return ResponseEntity.ok(mediaService.getMedia(placeId, type, page, size));
 }
}
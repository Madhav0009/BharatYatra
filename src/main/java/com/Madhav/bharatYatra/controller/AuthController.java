package com.Madhav.bharatYatra.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Madhav.bharatYatra.dto.AuthResponse;
import com.Madhav.bharatYatra.dto.LoginRequest;
import com.Madhav.bharatYatra.dto.RegisterRequest;
import com.Madhav.bharatYatra.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

//══════════════════════════════════════════════════════
//AUTH CONTROLLER  →  /api/auth
//══════════════════════════════════════════════════════
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

 private final AuthService authService;

 /** POST /api/auth/register */
 
 @PostMapping("/register")
 public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
     return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req));
 }

 /** POST /api/auth/login */
 
 @PostMapping("/login")
 public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
     return ResponseEntity.ok(authService.login(req));
 }
}

